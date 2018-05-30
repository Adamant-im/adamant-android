package com.dremanovich.adamant_android.presenters;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.dremanovich.adamant_android.interactors.ChatsInteractor;
import com.dremanovich.adamant_android.ui.entities.Chat;
import com.dremanovich.adamant_android.ui.entities.Message;
import com.dremanovich.adamant_android.ui.mvp_view.MessagesView;

import java.sql.Date;
import java.sql.Timestamp;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class MessagesPresenter extends MvpPresenter<MessagesView>{
    private Router router;
    private ChatsInteractor interactor;
    private CompositeDisposable subscriptions;

    private Chat currentChat;

    public MessagesPresenter(Router router, ChatsInteractor interactor, CompositeDisposable subscriptions) {
        this.router = router;
        this.interactor = interactor;
        this.subscriptions = subscriptions;
    }

    public void onShowChat(Chat chat){
        currentChat = chat;
        getViewState().showChatMessages(chat);
    }

    public void onClickSendMessage(String message){
        //TODO: verify message length and balance

        if (currentChat != null){
            Message messageEntity = addUnsendedMessageToChat(message);
            Disposable subscription = interactor
                    .sendMessage(message, currentChat.getInterlocutorId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((transaction -> {
                        if (transaction.isSuccess()){
                            messageEntity.setProcessed(false);
                        }
                        Log.d("NEW TRANSACTION", transaction.toString());
                    }));

            subscriptions.add(subscription);
        }

    }

    private Message addUnsendedMessageToChat(String message) {
        Message messageEntity = new Message();
        messageEntity.setiSay(true);
        messageEntity.setMessage(message);
        messageEntity.setDate("Сейчас");
        messageEntity.setProcessed(true);

        if (currentChat != null){
            currentChat.getMessages().add(messageEntity);
        }

        return messageEntity;
    }
}
