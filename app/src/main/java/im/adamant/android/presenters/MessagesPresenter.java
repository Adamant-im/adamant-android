package im.adamant.android.presenters;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;

import im.adamant.android.Screens;
import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.exceptions.MessageTooShortException;
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.helpers.BalanceConvertHelper;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.RefreshChatsInteractor;
import im.adamant.android.interactors.SendMessageInteractor;
import im.adamant.android.helpers.ChatsStorage;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import im.adamant.android.ui.messages_support.SupportedMessageTypes;
import im.adamant.android.ui.messages_support.factories.MessageFactory;
import im.adamant.android.ui.messages_support.factories.MessageFactoryProvider;
import im.adamant.android.ui.mvp_view.MessagesView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class MessagesPresenter extends BasePresenter<MessagesView>{
    private Router router;
    private SendMessageInteractor sendMessageInteractor;
    private RefreshChatsInteractor refreshChatsInteractor;
    private ChatsStorage chatsStorage;
    private MessageFactoryProvider messageFactoryProvider;
    private AccountInteractor accountInteractor;

    private Chat currentChat;
    private List<AbstractMessage> messages;
    private int currentMessageCount = 0;

    private Disposable syncSubscription;

    public MessagesPresenter(
            Router router,
            SendMessageInteractor sendMessageInteractor,
            RefreshChatsInteractor refreshChatsInteractor,
            MessageFactoryProvider messageFactoryProvider,
            AccountInteractor accountInteractor,
            ChatsStorage chatsStorage,
            CompositeDisposable subscriptions
    ) {
        super(subscriptions);
        this.router = router;
        this.sendMessageInteractor = sendMessageInteractor;
        this.refreshChatsInteractor = refreshChatsInteractor;
        this.messageFactoryProvider = messageFactoryProvider;
        this.accountInteractor = accountInteractor;
        this.chatsStorage = chatsStorage;
    }


    @Override
    public void attachView(MessagesView view) {
        super.attachView(view);

        syncSubscription = refreshChatsInteractor
                .execute()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError((error) -> {
                    if (error instanceof NotAuthorizedException){
                        router.navigateTo(Screens.SPLASH_SCREEN);
                    } else {
                        router.showSystemMessage(error.getMessage());
                    }
                    Log.e("Messages", error.getMessage(), error);
                })
                .doOnComplete(() -> {
                    if (currentMessageCount != messages.size()){
                        getViewState().showChatMessages(messages);
                        currentMessageCount = messages.size();
                    }
                })
                .retryWhen((retryHandler) -> retryHandler.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS, TimeUnit.SECONDS))
                .repeatWhen((completed) -> completed.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS, TimeUnit.SECONDS))
                .subscribe();

        subscriptions.add(syncSubscription);
    }

    @Override
    public void detachView(MessagesView view) {
        super.detachView(view);
        if (syncSubscription != null){
            syncSubscription.dispose();
        }
    }

    public void onShowChat(Chat chat){
        currentChat = chat;
        messages = chatsStorage.getMessagesByCompanionId(
                chat.getCompanionId()
        );

        getViewState().changeTitle(currentChat.getTitle());

        getViewState()
            .showChatMessages(
                messages
            );
    }

    public void onResume() {
        if (currentChat != null){
            getViewState().changeTitle(currentChat.getTitle());
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

        onShowChat(chat);
    }

    public void onClickSendMessage(String message){
        if (message.trim().isEmpty()) {
            //TODO: notify user about empty message
            return;
        }

        if (currentChat != null){
            AbstractMessage messageEntity = addAdamantBasicMessage(message);
            Disposable subscription = sendMessageInteractor
                    .sendMessage(message, currentChat.getCompanionId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((transaction -> {
                            if (transaction.isSuccess()){
                                messageEntity.setProcessed(true);
                                messageEntity.setTransactionId(transaction.getTransactionId());
                            }

                            getViewState().messageWasSended(messageEntity);
                        }),
                        (error) -> {
                            router.showSystemMessage(error.getMessage());
                            error.printStackTrace();
                        }
                    );

            subscriptions.add(subscription);

            getViewState().goToLastMessage();
        }

    }

    public void onChangeMessageText(String text) {
        long cost = sendMessageInteractor.calculateMessageCost(text);
        getViewState().showMessageCost(BalanceConvertHelper.convert(cost).toString());
    }

    public void onClickShowCompanionDetail() {
        router.navigateTo(Screens.COMPANION_DETAIL_SCREEN, currentChat.getCompanionId());
    }

    private AbstractMessage addAdamantBasicMessage(String message) {
        AbstractMessage abstractMessage = null;
        try {
            MessageFactory messageFactory = messageFactoryProvider.getFactoryByType(SupportedMessageTypes.ADAMANT_BASIC);
            abstractMessage = messageFactory.getMessageBuilder().build(
                    null,
                    message, true,
                    System.currentTimeMillis(),
                    currentChat.getCompanionId()
            );

            chatsStorage.addMessageToChat(abstractMessage);

        } catch (Exception e) {
            e.printStackTrace();
            router.showSystemMessage(e.getMessage());
        }


        return abstractMessage;
    }

}
