package im.adamant.android.interactors.chats;

import java.util.concurrent.TimeUnit;

import im.adamant.android.BuildConfig;
import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.transaction_assets.NotUsedAsset;
import im.adamant.android.core.entities.transaction_assets.TransactionAsset;
import im.adamant.android.core.entities.transaction_assets.TransactionChatAsset;
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.core.responses.TransactionList;
import im.adamant.android.helpers.LoggerHelper;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Emitter;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class NewTransactionsSource {
    private AdamantApiWrapper api;
    private int maxHeight = 1;

    public NewTransactionsSource(AdamantApiWrapper api) {
        this.api = api;
    }

    public Flowable<Transaction<? super TransactionAsset>> execute(int startHeight) {
        if (!api.isAuthorized()){return Flowable.error(new NotAuthorizedException("Not authorized"));}

        maxHeight = startHeight;

        return Flowable.defer(() -> getTransactionsBatch(maxHeight, 0).concatWith(getAllAdamantTransfers(maxHeight, 0)))
                .doOnNext(transaction -> {
                    if (transaction.getHeight() > maxHeight) {
                        maxHeight = transaction.getHeight();
                    }
                })
                .retryWhen((retryHandler) -> retryHandler.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS, TimeUnit.SECONDS));
    }

    private Flowable<Transaction<? super TransactionAsset>> getTransactionsBatch(int height, int offset) {
        return api
                .getMessageTransactionsByHeightAndOffset(height, offset, AdamantApi.ORDER_BY_TIMESTAMP_ASC)
                .observeOn(Schedulers.computation())
                .flatMap(transactionList -> {
                    if (transactionList.isSuccess()) {
                        int count = transactionList.getCount();
                        int newOffset = offset + AdamantApi.MAX_TRANSACTIONS_PER_REQUEST;

                        Flowable<Transaction<? super TransactionAsset>> result = Flowable
                                .fromIterable(transactionList.getTransactions());

                        if (newOffset <= count) {
                            return result.concatWith(getTransactionsBatch(height, newOffset));
                        }

                        return result;
                    } else {
                        return Flowable.error(new Exception(transactionList.getError()));
                    }
                })
                .doOnError(error -> LoggerHelper.e(getClass().getSimpleName(), error.getMessage(), error));
    }

    private Flowable<Transaction<? super TransactionAsset>> getAllAdamantTransfers(int height, int offset) {
        return  api
                    .getAdamantTransactions(Transaction.SEND, height, offset, AdamantApi.ORDER_BY_TIMESTAMP_ASC)
                    .observeOn(Schedulers.computation())
                    .flatMap(transactionList -> {
                        if (transactionList.isSuccess()){
                            int count = transactionList.getCount();
                            int newOffset = offset + AdamantApi.MAX_TRANSACTIONS_PER_REQUEST;

                            Flowable<Transaction<? super TransactionAsset>> result = Flowable
                                    .fromIterable(transactionList.getTransactions());

                            if (newOffset <= count) {
                                return result.concatWith(getAllAdamantTransfers(height, newOffset));
                            }

                            return result;
                        } else {
                            return Flowable.error(new Exception(transactionList.getError()));
                        }
                    })
                    .timeout(BuildConfig.DEFAULT_OPERATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);

    }
}
