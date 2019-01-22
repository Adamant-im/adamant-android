package im.adamant.android.interactors;

import java.util.concurrent.TimeUnit;

import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.transaction_assets.TransactionChatAsset;
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.core.responses.TransactionList;
import im.adamant.android.helpers.ChatsStorage;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import im.adamant.android.ui.mappers.LocalizedChatMapper;
import im.adamant.android.ui.mappers.LocalizedMessageMapper;
import im.adamant.android.ui.mappers.TransactionToChatMapper;
import im.adamant.android.ui.mappers.TransactionToMessageMapper;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Emitter;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class RefreshChatsInteractor {
    private AdamantApiWrapper api;
    private TransactionToChatMapper chatMapper;
    private TransactionToMessageMapper messageMapper;
    private LocalizedMessageMapper localizedMessageMapper;
    private LocalizedChatMapper localizedChatMapper;

    private ChatsStorage chatsStorage;

    private int countMessageItems = 0;
    private int countTransactionItems = 0;
    private int currentMessageHeight = 1;
    private int currentTransactionHeight = 1;
    private int offsetMessageItems = 0;

    private long subscribers = 0;
    private boolean isCompleted = false;

    private Disposable subscription;
    private PublishSubject<Irrelevant> publisher = PublishSubject.create();
    private Flowable<Irrelevant> executeFlowable;

    public enum Irrelevant { INSTANCE }

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

        //TODO: Counting the number of subscribers in a variable is an ugly decision. Think more
        executeFlowable = publisher
                .doOnDispose(() -> {
                    if (subscribers > 0) { subscribers--; }
                })
                .doOnSubscribe(disposable -> {
                    subscribers++;
                })
                .toFlowable(BackpressureStrategy.LATEST);
    }

    public Flowable<Irrelevant> execute() {
        //TODO: Schedulers must be injected through Dagger for comfort unit-testing

        //TODO: The current height should be "Atomic" changed

        //TODO: Use database for save received transactions

        //TODO: Well test the erroneous execution path, replace where you need doOnError

//        if (!api.isAuthorized()){return Completable.error(new NotAuthorizedException("Not authorized"));}
        if (api.isAuthorized()) {

            boolean isNewStart = (subscription == null) || isCompleted;

            if (isNewStart){
                isCompleted = false;

                if (subscription != null){
                    subscription.dispose();
                }

                // This complex construction is necessary in order to notify clients of events,
                // but to prohibit the interruption of synchronization.
                subscription = getBatchAdamantMessages(offsetMessageItems)
                        .concatWith(getAllAdamantTransfers())
                        .toSortedList()
                        .toFlowable()
                        .flatMap(list -> Flowable.just(list).flatMapIterable(item -> item))
                        .doOnNext(message -> {
                            message = localizedMessageMapper.apply(message);
                            chatsStorage.addMessageToChat(message);
                        })
                        .doOnError(error -> {
                            LoggerHelper.e("CHAT TRANS", error.getMessage(), error);
                            publisher.onError(error);
                        })
                        .doOnComplete(() -> {
                            isCompleted = true;
                            chatsStorage.updateLastMessages();
                            publisher.onNext(Irrelevant.INSTANCE);
                        })
                        .retryWhen((retryHandler) -> retryHandler.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS, TimeUnit.SECONDS))
                        .repeatWhen((completed) -> completed
                                .delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS, TimeUnit.SECONDS)
                                .filter(nothing -> subscribers > 0))
                        .subscribe();
            }

        } else {
            publisher.onError(new NotAuthorizedException("Not authorized"));
        }

        return executeFlowable;
    }

    public void cleanUp() {
        if (subscription != null) {
            subscription.dispose();
        }
        subscription = null;

        isCompleted = false;

        countMessageItems = 0;
        countTransactionItems = 0;
        currentMessageHeight = 1;
        currentTransactionHeight = 1;
        offsetMessageItems = 0;
        chatsStorage.cleanUp();
    }

    private Flowable<AbstractMessage> getBatchAdamantMessages(int offset) {
        return Flowable
                .defer(() -> Flowable.just(currentMessageHeight))
                .flatMap((height) -> {
                    Flowable<TransactionList<TransactionChatAsset>> transactionFlowable = null;
                    if (offset > 0){
                        transactionFlowable = api.getTransactions(AdamantApi.ORDER_BY_TIMESTAMP_ASC, offset);
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
                                countMessageItems++;
                                if (transaction.getHeight() > currentMessageHeight) {
                                    currentMessageHeight = transaction.getHeight();
                                }
                            })
                            .flatMap(transaction -> Flowable.just(messageMapper.apply(transaction)))
                            .concatWith(Flowable.defer(() -> {
                                boolean noRepeat = countMessageItems < AdamantApi.MAX_TRANSACTIONS_PER_REQUEST;
                                if (noRepeat) {
                                    countMessageItems = 0;
                                    offsetMessageItems = 0;
                                    return Flowable.create(Emitter::onComplete, BackpressureStrategy.DROP);
                                } else {
                                    offsetMessageItems += countMessageItems;
                                    countMessageItems = 0;
                                    return getBatchAdamantMessages(offsetMessageItems);
                                }
                            }));
                });
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
                    countTransactionItems++;
                    if (transaction.getHeight() > currentTransactionHeight) {
                        currentTransactionHeight = transaction.getHeight();
                    }
                })
                .flatMap(transaction -> Flowable.just(messageMapper.apply(transaction)))
                .concatWith(Flowable.defer(() -> {
                    boolean noRepeat = countTransactionItems < AdamantApi.MAX_TRANSACTIONS_PER_REQUEST;
                    if (noRepeat){
                        countTransactionItems = 0;
                        return Flowable.create(Emitter::onComplete, BackpressureStrategy.DROP);
                    } else {
                        countTransactionItems = 0;
                        return getAllAdamantTransfers();
                    }
                }));
    }

    @Override
    protected void finalize() throws Throwable {
        subscription.dispose();
        super.finalize();
    }
}
