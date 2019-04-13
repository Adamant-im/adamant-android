package im.adamant.android.interactors.chats;

import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.transaction_assets.TransactionAsset;
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.core.responses.MessageList;
import im.adamant.android.helpers.LoggerHelper;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Emitter;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class HistoryTransactionsSource {
    private AdamantApiWrapper api;

    public HistoryTransactionsSource(AdamantApiWrapper api) {
        this.api = api;
    }

    public Flowable<Transaction<? super TransactionAsset>> execute(String chatId) {
        if (!api.isAuthorized()){return Flowable.error(new NotAuthorizedException("Not authorized"));}

        return getTransactionsBatch(chatId, 0);
    }


    private Flowable<Transaction<? super TransactionAsset>> getTransactionsBatch(String chatId, int offset) {
        Flowable<MessageList> transactionFlowable = null;
        if (offset > 0){
            transactionFlowable = api.getMessagesByOffset(chatId, offset, AdamantApi.ORDER_BY_TIMESTAMP_ASC);
        } else {
            transactionFlowable = api.getMessages(chatId, AdamantApi.ORDER_BY_TIMESTAMP_ASC);
        }

        return transactionFlowable
                .observeOn(Schedulers.computation())
                .flatMap(transactionList -> {
                    if (transactionList.isSuccess()) {
                        int count = transactionList.getCount();
                        int newOffset = offset + AdamantApi.MAX_TRANSACTIONS_PER_REQUEST;

                        Flowable<Transaction<? super TransactionAsset>> result = Flowable
                                .fromIterable(transactionList.getMessages());

                        if (newOffset <= count) {
                            return result.concatWith(getTransactionsBatch(chatId, newOffset));
                        }

                        return result;
                    } else {
                        return Flowable.error(new Exception(transactionList.getError()));
                    }
                })
                .doOnError(error -> LoggerHelper.e(getClass().getSimpleName(), error.getMessage(), error));
    }
}
