package im.adamant.android.interactors;

import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.core.responses.ChatList;
import im.adamant.android.helpers.ChatsStorage;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.mappers.ChatTransactionToChatMapper;
import im.adamant.android.ui.mappers.TransactionToMessageMapper;
import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class GetChatListInteractor {
    private AdamantApiWrapper api;
    private ChatTransactionToChatMapper chatMapper;
    private TransactionToMessageMapper messageMapper;

    private ChatsStorage chatsStorage;

    private int countItems = 0;
    private int currentHeight = 1;
    private int offsetItems = 0;

    public GetChatListInteractor(
            AdamantApiWrapper api,
            ChatTransactionToChatMapper chatMapper,
            TransactionToMessageMapper messageMapper,
            ChatsStorage chatsStorage
    ) {
        this.api = api;
        this.chatMapper = chatMapper;
        this.chatsStorage = chatsStorage;
        this.messageMapper = messageMapper;
    }

    public Completable execute() {
        //TODO: Schedulers must be injected through Dagger for comfort unit-testing

        //TODO: The current height should be "Atomic" changed

        //TODO: Use database for save received transactions

        //TODO: Well test the erroneous execution path, replace where you need doOnError

        if (!api.isAuthorized()){return Completable.error(new NotAuthorizedException("Not authorized"));}

        return Flowable
                .defer(() -> Flowable.just(currentHeight))
                .flatMap((height) -> {
                    Flowable<ChatList> transactionFlowable = null;
                    if (offsetItems > 0){
                        transactionFlowable = api.getChatsByOffset(offsetItems, AdamantApi.ORDER_BY_TIMESTAMP_DESC);
                    } else {
                        transactionFlowable = api.getChats(AdamantApi.ORDER_BY_TIMESTAMP_DESC);
                    }

                    return transactionFlowable
                            .observeOn(Schedulers.computation())
                            .flatMap(transactionList -> {
                                if (transactionList.isSuccess()){
                                    return Flowable.fromIterable(transactionList.getChats());
                                } else {
                                    return Flowable.error(new Exception(transactionList.getError()));
                                }
                            })
                            .doOnNext(transaction -> {
                                Chat chat = chatMapper.apply(transaction);
                                AbstractMessage message = messageMapper.apply(transaction);
                                chat.setLastMessage(message);
                                //TODO: Inject predefined chats
                                //chat = localizedChatMapper.apply(chat);
                                chatsStorage.addNewChat(chat);
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
