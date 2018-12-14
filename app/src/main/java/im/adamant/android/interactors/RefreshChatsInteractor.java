package im.adamant.android.interactors;

import org.reactivestreams.Publisher;

import androidx.constraintlayout.solver.widgets.Flow;
import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.transaction_assets.NotUsedAsset;
import im.adamant.android.core.entities.transaction_assets.TransactionAsset;
import im.adamant.android.core.entities.transaction_assets.TransactionChatAsset;
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.core.responses.TransactionList;
import im.adamant.android.helpers.ChatsStorage;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import im.adamant.android.ui.mappers.LocalizedChatMapper;
import im.adamant.android.ui.mappers.LocalizedMessageMapper;
import im.adamant.android.ui.mappers.TransactionToChatMapper;
import im.adamant.android.ui.mappers.TransactionToMessageMapper;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class RefreshChatsInteractor {
    private AdamantApiWrapper api;
    private TransactionToChatMapper chatMapper;
    private TransactionToMessageMapper messageMapper;
    private LocalizedMessageMapper localizedMessageMapper;
    private LocalizedChatMapper localizedChatMapper;

    private ChatsStorage chatsStorage;

    private int countItems = 0;
    private int currentMessageHeight = 1;
    private int currentTransactionHeight = 1;
    private int offsetMessageItems = 0;

    public RefreshChatsInteractor(
            AdamantApiWrapper api,
            TransactionToChatMapper chatMapper,
            TransactionToMessageMapper messageMapper,
            LocalizedMessageMapper localizedMessageMapper,
            LocalizedChatMapper localizedChatMapper,
            ChatsStorage chatsStorage
    ) {
        this.api = api;
        this.chatMapper = chatMapper;
        this.messageMapper = messageMapper;
        this.localizedMessageMapper = localizedMessageMapper;
        this.localizedChatMapper = localizedChatMapper;
        this.chatsStorage = chatsStorage;
    }

    public Completable execute() {
        //TODO: Schedulers must be injected through Dagger for comfort unit-testing

        //TODO: The current height should be "Atomic" changed

        //TODO: Use database for save received transactions

        //TODO: Well test the erroneous execution path, replace where you need doOnError

        if (!api.isAuthorized()){return Completable.error(new NotAuthorizedException("Not authorized"));}

        return Flowable
                .defer(() -> Flowable.just(currentMessageHeight))
                .flatMap((height) -> {
                    Flowable<TransactionList<TransactionChatAsset>> transactionFlowable = null;
                    if (offsetMessageItems > 0){
                        transactionFlowable = api.getTransactions(AdamantApi.ORDER_BY_TIMESTAMP_ASC, offsetMessageItems);
                    } else {
                        transactionFlowable = api.getTransactions(height, AdamantApi.ORDER_BY_TIMESTAMP_ASC);
                    }

                    return transactionFlowable
                            .observeOn(Schedulers.computation())
                            .flatMap(transactionList -> {
                                if (transactionList.isSuccess()){
                                    return Flowable.fromIterable(transactionList.getTransactions());
                                } else {
                                    return Flowable.error(new Exception(transactionList.getError()));
                                }
                            })
                            .doOnNext(transaction -> {
                                Chat chat = chatMapper.apply(transaction);
                                chat = localizedChatMapper.apply(chat);
                                chatsStorage.addNewChat(chat);
                            })
                            .doOnNext(transaction -> {
                                countItems++;
                                if (transaction.getHeight() > currentMessageHeight) {
                                    currentMessageHeight = transaction.getHeight();
                                }
                            })
                            .flatMap(transaction -> Flowable.just(messageMapper.apply(transaction)))
                            .mergeWith(getAllAdamantTransfers())
                            .toSortedList()
                            .toFlowable()
                            .flatMap(list -> Flowable.just(list).flatMapIterable(item -> item))
                            .doOnNext(message -> {
                                message = localizedMessageMapper.apply(message);
                                chatsStorage.addMessageToChat(message);
                            })
                            .doOnError(Throwable::printStackTrace)
                            .doOnComplete(() -> {
                                chatsStorage.updateLastMessages();
                            });
                })
                .repeatUntil(() -> {
                    boolean noRepeat = countItems < AdamantApi.MAX_TRANSACTIONS_PER_REQUEST;
                    if (noRepeat){
                        countItems = 0;
                        offsetMessageItems = 0;
                    } else {
                        offsetMessageItems += countItems;
                        countItems = 0;

                    }
                    return  noRepeat;
                })
                .ignoreElements();
    }

    public void cleanUp() {
        countItems = 0;
        currentMessageHeight = 1;
        offsetMessageItems = 0;
        chatsStorage.cleanUp();
    }

    private Flowable<AbstractMessage> getAllAdamantTransfers() {
        return Flowable
                .defer(() -> Flowable.just(currentTransactionHeight))
                .flatMap((height) -> api
                        .getAdamantTransactions(Transaction.SEND, height, AdamantApi.ORDER_BY_TIMESTAMP_ASC)
                        .observeOn(Schedulers.computation())
                        .flatMap(transactionList -> {
                            if (transactionList.isSuccess()){
                                return Flowable.fromIterable(transactionList.getTransactions());
                            } else {
                                return Flowable.error(new Exception(transactionList.getError()));
                            }
                        }))
                .doOnNext(transaction -> {
                    countItems++;
                    if (transaction.getHeight() > currentTransactionHeight) {
                        currentTransactionHeight = transaction.getHeight();
                    }
                })
                .flatMap(transaction -> Flowable.just(messageMapper.apply(transaction)));
    }
}
