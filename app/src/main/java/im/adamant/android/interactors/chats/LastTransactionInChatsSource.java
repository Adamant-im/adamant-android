package im.adamant.android.interactors.chats;

import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.transaction_assets.TransactionAsset;
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.core.responses.ChatList;
import im.adamant.android.helpers.LoggerHelper;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Emitter;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class LastTransactionInChatsSource {
    private AdamantApiWrapper api;

    public LastTransactionInChatsSource(AdamantApiWrapper api) {
        this.api = api;
    }

    public Flowable<ChatList.ChatDescription> execute() {
        if (!api.isAuthorized()){return Flowable.error(new NotAuthorizedException("Not authorized"));}

        return getTransactionsBatch(0);
    }


    private Flowable<ChatList.ChatDescription> getTransactionsBatch(int offset) {
        Flowable<ChatList> transactionFlowable = null;
        if (offset > 0){
            transactionFlowable = api.getChatsByOffset(offset, AdamantApi.ORDER_BY_TIMESTAMP_DESC);
        } else {
            transactionFlowable = api.getChats(AdamantApi.ORDER_BY_TIMESTAMP_DESC);
        }

        return transactionFlowable
                .observeOn(Schedulers.computation())
                .flatMap(transactionList -> {
                    if (transactionList.isSuccess()) {
                        int count = transactionList.getCount();
                        int newOffset = offset + AdamantApi.MAX_TRANSACTIONS_PER_REQUEST;

                        Flowable<ChatList.ChatDescription> result = Flowable
                                .fromIterable(transactionList.getChats());

                        if (newOffset <= count) {
                            return result.concatWith(getTransactionsBatch(newOffset));
                        }

                        return result;
                    } else {
                        return Flowable.error(new Exception(transactionList.getError()));
                    }
                })
                .doOnError(error -> LoggerHelper.e(getClass().getSimpleName(), error.getMessage(), error));
    }
}
