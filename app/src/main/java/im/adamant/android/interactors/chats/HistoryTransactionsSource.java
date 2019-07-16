package im.adamant.android.interactors.chats;

import androidx.annotation.MainThread;

import java.util.concurrent.TimeUnit;

import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.transaction_assets.TransactionAsset;
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.core.responses.MessageList;
import im.adamant.android.helpers.LoggerHelper;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class HistoryTransactionsSource {
    private AdamantApiWrapper api;

    public HistoryTransactionsSource(AdamantApiWrapper api) {
        this.api = api;
    }

    public Flowable<Transaction<? super TransactionAsset>> execute(String chatId, int offset, int limit) {
        if (!api.isAuthorized()) {
            return Flowable.error(new NotAuthorizedException("Not authorized"));
        }
        return getTransactionsBatch(chatId, offset, limit);
    }

    private int count = Integer.MAX_VALUE;

    @MainThread
    private void setCount(int count){
        this.count = count;
    }

    @MainThread
    public int getCount() {
        return count;
    }

    private Flowable<Transaction<? super TransactionAsset>> getTransactionsBatch(String chatId, int offset, int limit) {
        Flowable<MessageList> transactionFlowable =
                api.getMessagesByOffset(chatId, offset, limit, AdamantApi.ORDER_BY_TIMESTAMP_ASC);

        return transactionFlowable
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(transactionList -> {
                    if (transactionList.isSuccess()) {
                        setCount(transactionList.getCount());
                    }
                })
                .observeOn(Schedulers.computation())
                .flatMap(transactionList -> {
                    if (transactionList.isSuccess()) {
                        return Flowable
                                .fromIterable(transactionList.getMessages());
                    } else {
                        return Flowable.error(new Exception(transactionList.getError()));
                    }
                })
                .doOnError(error -> LoggerHelper.e(getClass().getSimpleName(), error.getMessage(), error))
                .retryWhen(throwableFlowable -> throwableFlowable.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS, TimeUnit.SECONDS));
    }
}
