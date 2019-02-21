package im.adamant.android.interactors;

import java.util.concurrent.TimeUnit;

import im.adamant.android.BuildConfig;
import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.helpers.Settings;
import io.reactivex.Flowable;

public class HasNewMessagesInteractor {
    private AdamantApiWrapper api;
    private Settings settings;

    public enum Event {
        HAS_NEW_MESSAGES,
        NO_NEW_MESSAGES
    }

    public HasNewMessagesInteractor(AdamantApiWrapper api, Settings settings) {
        this.api = api;
        this.settings = settings;
    }

    public Flowable<Event> execute() {
        return api.getTransactions(1, AdamantApi.ORDER_BY_TIMESTAMP_DESC)
                .flatMap((transactions) -> {
                    LoggerHelper.d("HAS_NEW_MESS", "GET TRANSACTIONS");
                    if (transactions.isSuccess()) {
                        return Flowable.just(transactions.getTransactions());
                    } else {
                        return Flowable.error(new Exception());
                    }
                })
                .flatMapIterable(x -> x)
                .take(1)
                .flatMap(transaction -> {
                    int lastTimestamp = settings.getLastTransactionTimestamp();
                    LoggerHelper.d("HAS_NEW_MESS", "TRANSACTION TIMESTAMP: " + transaction.getTimestamp());
                    LoggerHelper.d("HAS_NEW_MESS", "LAST SAVED TIMESTAMP: " + lastTimestamp);
                   if (transaction.getTimestamp() > lastTimestamp) {
                       settings.setLastTransactionTimestamp(transaction.getTimestamp());
                       return Flowable.just(Event.HAS_NEW_MESSAGES);
                   } else {
                       return Flowable.just(Event.NO_NEW_MESSAGES);
                   }
                })
                .timeout(BuildConfig.DEFAULT_OPERATION_TIMEOUT_SECONDS * 3, TimeUnit.SECONDS);
    }
}
