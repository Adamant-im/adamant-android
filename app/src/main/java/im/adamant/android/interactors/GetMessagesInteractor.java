package im.adamant.android.interactors;

import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.core.responses.ChatList;
import im.adamant.android.core.responses.MessageList;
import im.adamant.android.helpers.ChatsStorage;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.mappers.TransactionToMessageMapper;
import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class GetMessagesInteractor {
    private AdamantApiWrapper api;
    private TransactionToMessageMapper messageMapper;
    private ChatsStorage chatsStorage;

    private int countItems = 0;
    private int currentHeight = 1;
    private int offsetItems = 0;

    public GetMessagesInteractor(
            AdamantApiWrapper api,
            TransactionToMessageMapper messageMapper,
            ChatsStorage chatsStorage
    ) {
        this.api = api;
        this.messageMapper = messageMapper;
        this.chatsStorage = chatsStorage;
    }

    //TODO: Теперь за атрибутом height лучше следить в ChatStorage. Т.к. обновления лучше подкачивать с помощью RefreshIneractor'a
    public Completable execute(String chatId) {
        if (!api.isAuthorized()){return Completable.error(new NotAuthorizedException("Not authorized"));}

        return Flowable
                .defer(() -> Flowable.just(currentHeight))
                .flatMap((height) -> {
                    Flowable<MessageList> transactionFlowable = null;
                    if (offsetItems > 0){
                        transactionFlowable = api.getMessagesByOffset(chatId, offsetItems, AdamantApi.ORDER_BY_TIMESTAMP_DESC);
                    } else {
                        transactionFlowable = api.getMessages(chatId, AdamantApi.ORDER_BY_TIMESTAMP_DESC);
                    }

                    return transactionFlowable
                            .observeOn(Schedulers.computation())
                            .flatMap(transactionList -> {
                                if (transactionList.isSuccess()){
                                    return Flowable.fromIterable(transactionList.getMessages());
                                } else {
                                    return Flowable.error(new Exception(transactionList.getError()));
                                }
                            })
                            .doOnNext(transaction -> {
                                AbstractMessage message = messageMapper.apply(transaction);
                                chatsStorage.addMessageToChat(message);
                            })
                            .doOnNext(transaction -> {
                                countItems++;
                                if (transaction.getHeight() > currentHeight) {
                                    currentHeight = transaction.getHeight();
                                }
                            })
                            .doOnError(Throwable::printStackTrace);
                })
                .repeatUntil(() -> {
                    boolean noRepeat = countItems < AdamantApi.MAX_TRANSACTIONS_PER_REQUEST;
                    if (noRepeat){
                        countItems = 0;
                        offsetItems = 0;
                    } else {
                        offsetItems += countItems;
                        countItems = 0;

                    }
                    return  noRepeat;
                })
                .ignoreElements();
    }
}
