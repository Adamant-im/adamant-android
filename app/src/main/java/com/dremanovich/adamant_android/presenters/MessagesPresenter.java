package com.dremanovich.adamant_android.presenters;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.dremanovich.adamant_android.core.AdamantApi;
import com.dremanovich.adamant_android.interactors.ChatsInteractor;
import com.dremanovich.adamant_android.ui.entities.Chat;
import com.dremanovich.adamant_android.ui.entities.Message;
import com.dremanovich.adamant_android.ui.mvp_view.MessagesView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class MessagesPresenter extends BasePresenter<MessagesView>{
    private Router router;
    private ChatsInteractor interactor;

    private Chat currentChat;
    private List<Message> messages;
    private int currentMessageCount = 0;

    private Disposable syncSubscription;

    public MessagesPresenter(Router router, ChatsInteractor interactor, CompositeDisposable subscriptions) {
        super(subscriptions);
        this.router = router;
        this.interactor = interactor;
    }


    @Override
    public void attachView(MessagesView view) {
        super.attachView(view);

        syncSubscription = interactor
                .synchronizeWithBlockchain()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError((error) -> {
                    router.showSystemMessage(error.getMessage());
                    Log.e("Messages", error.getMessage(), error);
                })
                .doOnComplete(() -> {
                    if (currentMessageCount != messages.size()){
                        getViewState().showChatMessages(messages);
                        currentMessageCount = messages.size();
                    }
                })
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
        messages = interactor.getMessagesByCompanionId(
                chat.getCompanionId()
        );

        getViewState().changeTitle(currentChat.getCompanionId());

        getViewState()
            .showChatMessages(
                messages
            );
    }

    public void onClickSendMessage(String message){
        //TODO: verify message length and balance

        if (currentChat != null){
            Message messageEntity = addUnsendedMessageToChat(message);
            Disposable subscription = interactor
                    .sendMessage(message, currentChat.getCompanionId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((transaction -> {
                        if (transaction.isSuccess()){
                            messageEntity.setProcessed(true);
                            messageEntity.setTransactionId(transaction.getTransactionId());
                        }
                    }));

            subscriptions.add(subscription);

            getViewState().goToLastMessage();
        }

    }

    private Message addUnsendedMessageToChat(String message) {
        Message messageEntity = new Message();
        messageEntity.setiSay(true);
        messageEntity.setMessage(message);
        messageEntity.setDate(System.currentTimeMillis());
        messageEntity.setProcessed(false);

        if (messages != null){
            messages.add(messageEntity);
        }

        return messageEntity;
    }
}
