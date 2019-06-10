package im.adamant.android.ui.presenters;


import com.arellomobile.mvp.InjectViewState;

import im.adamant.android.Screens;
import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.helpers.BalanceConvertHelper;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.ChatUpdatePublicKeyInteractor;
import im.adamant.android.interactors.chats.ChatInteractor;
import im.adamant.android.interactors.chats.ChatsStorage;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.entities.AdamantBasicMessage;
import im.adamant.android.ui.messages_support.entities.MessageListContent;
import im.adamant.android.ui.messages_support.factories.AdamantBasicMessageFactory;
import im.adamant.android.ui.messages_support.factories.MessageFactoryProvider;
import im.adamant.android.ui.messages_support.processors.MessageProcessor;
import im.adamant.android.ui.mvp_view.MessagesView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class MessagesPresenter extends ProtectedBasePresenter<MessagesView>{
    private ChatInteractor chatInteractor;
    private ChatsStorage chatsStorage;
    private MessageFactoryProvider messageFactoryProvider;
    private ChatUpdatePublicKeyInteractor chatUpdatePublicKeyInteractor;
    private AdamantApiWrapper api;

    private Chat currentChat;
    private List<MessageListContent> messages;
    private int currentMessageCount = 0;

    private Disposable syncSubscription;

    public MessagesPresenter(
            Router router,
            AccountInteractor accountInteractor,
            ChatInteractor chatInteractor,
            ChatUpdatePublicKeyInteractor chatUpdatePublicKeyInteractor,
            MessageFactoryProvider messageFactoryProvider,
            ChatsStorage chatsStorage,
            AdamantApiWrapper api
    ) {
        super(router, accountInteractor);
        this.chatInteractor = chatInteractor;
        this.chatUpdatePublicKeyInteractor = chatUpdatePublicKeyInteractor;
        this.messageFactoryProvider = messageFactoryProvider;
        this.chatsStorage = chatsStorage;
        this.api = api;
    }


    @Override
    public void attachView(MessagesView view) {
        super.attachView(view);

    }

    @Override
    public void detachView(MessagesView view) {
        super.detachView(view);

        if (syncSubscription != null){
            syncSubscription.dispose();
        }
    }

    public void onShowChatByCompanionId(String companionId){
        currentChat = chatsStorage.findChatByCompanionId(companionId);
        if (currentChat == null){return;}

        getViewState().changeTitles(currentChat.getTitle(), currentChat.getCompanionId());

        Disposable subscribe = chatInteractor
                .loadHistory(companionId)
                .ignoreElements()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    refreshMessageList(companionId);
                    getViewState().goToLastMessage();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            Disposable updateDisposable = chatInteractor
                                    .update()
                                    .doAfterSuccess(cnt -> {
                                        if (cnt > 0) {
                                            getViewState().showAvatarInTitle(currentChat.getCompanionPublicKey());
                                            refreshMessageList(companionId);
                                        }
                                    })
                                    .repeatWhen((completed) -> completed.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS, TimeUnit.SECONDS))
                                    .subscribe();
                            subscriptions.add(updateDisposable);
                        },
                        (error) -> {
                            router.showSystemMessage(error.getMessage());
                            LoggerHelper.e(getClass().getSimpleName(), error.getMessage(), error);
                        }
                );

        subscriptions.add(subscribe);

    }

    public void onResume() {
        if (currentChat != null) {
            getViewState().changeTitles(currentChat.getTitle(), currentChat.getCompanionId());
            getViewState().showAvatarInTitle(currentChat.getCompanionPublicKey());
        }
    }

    public void onClickCopyAddress() {
        if (currentChat != null) {
            getViewState().copyCompanionId(currentChat.getCompanionId());
        }
    }

    public void onClickShowQrCodeAddress() {
        if (currentChat != null) {
            getViewState().showQrCodeCompanionId(currentChat.getCompanionId());
        }
    }

    public void onShowChatByAddress(String address, String label){
        Chat chat = new Chat();
        chat.setCompanionId(address);
        if (label != null && !label.isEmpty()){
            chat.setTitle(label);
        } else {
            chat.setTitle(address);
        }

        chatUpdatePublicKeyInteractor.execute(chat);
        chatsStorage.addNewChat(chat);
        onShowChatByCompanionId(address);

    }

    public void onClickSendAdamantBasicMessage(String message){
        if (message.trim().isEmpty()) {
            //TODO: notify user about empty message
            return;
        }

        if (currentChat == null){return;}

        try {
            AdamantBasicMessageFactory messageFactory = (AdamantBasicMessageFactory) messageFactoryProvider.getFactoryByType(SupportedMessageListContentType.ADAMANT_BASIC);
            AdamantBasicMessage messageEntity = getAdamantMessage(message, messageFactory);

            chatsStorage.addMessageToChat(messageEntity);
            getViewState().showChatMessages(messages);

            MessageProcessor<AdamantBasicMessage> messageProcessor = messageFactory.getMessageProcessor();

            Disposable subscription = messageProcessor
                    .sendMessage(messageEntity)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((transaction -> {
                            getViewState().messageWasSended(messageEntity);
                        }),
                        (error) -> {
                            router.showSystemMessage(error.getMessage());
                            error.printStackTrace();
                        }
                    );

            subscriptions.add(subscription);

            getViewState().goToLastMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onChangeMessageText(String text) {
        //TODO: You need to navigate by the type of message that is being edited
        try {
            AdamantBasicMessageFactory messageFactory = (AdamantBasicMessageFactory) messageFactoryProvider.getFactoryByType(SupportedMessageListContentType.ADAMANT_BASIC);
            AdamantBasicMessage messageEntity = getAdamantMessage(text, messageFactory);

            long cost = messageFactory.getMessageProcessor().calculateMessageCostInAdamant(messageEntity);
            getViewState().showMessageCost(BalanceConvertHelper.convert(cost).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private AdamantBasicMessage getAdamantMessage(String message, AdamantBasicMessageFactory messageFactory) {
        AdamantBasicMessage abstractMessage = null;
        try {
            String publicKey = api.getAccount().getPublicKey();
            abstractMessage = messageFactory.getMessageBuilder().build(
                    null,
                    message, true,
                    System.currentTimeMillis(),
                    currentChat.getCompanionId(),
                    publicKey
            );
        } catch (Exception e) {
            e.printStackTrace();
            router.showSystemMessage(e.getMessage());
        }

        return abstractMessage;
    }

    private void refreshMessageList(String companionId) {
        messages = chatsStorage.getMessagesByCompanionId(
                companionId
        );

        getViewState().showChatMessages(messages);
    }

    public void onClickShowRenameDialog() {
        if (currentChat != null){
            if (currentChat.getCompanionId().equalsIgnoreCase(currentChat.getTitle())){
                getViewState().showRenameDialog(currentChat.getCompanionId());
            } else {
                getViewState().showRenameDialog(currentChat.getTitle());
            }
        }
    }

    public void onClickRenameButton(String newName) {
        if (currentChat != null){
            currentChat.setTitle(newName);

            getViewState().changeTitles(newName, currentChat.getCompanionId());
            getViewState().startSavingContacts();
        }
    }

    public void onClickSendCurrencyButton() {
        if (currentChat != null){
            router.navigateTo(Screens.SEND_CURRENCY_TRANSFER_SCREEN, currentChat.getCompanionId());
        }
    }
}
