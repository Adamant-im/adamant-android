package im.adamant.android.interactors.chats;

import androidx.annotation.MainThread;

import java.util.concurrent.TimeUnit;

import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.core.responses.ChatList;
import im.adamant.android.helpers.LoggerHelper;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LastTransactionInChatsSource {
    private AdamantApiWrapper api;

    public LastTransactionInChatsSource(AdamantApiWrapper api) {
        this.api = api;
    }

    public Flowable<ChatList.ChatDescription> execute() {
        if (!api.isAuthorized()){return Flowable.error(new NotAuthorizedException("Not authorized"));}

        return getTransactionsBatch(0,AdamantApi.DEFAULT_TRANSACTIONS_LIMIT);
    }


    public Flowable<ChatList.ChatDescription> execute(int offset,int limit) {
        if (!api.isAuthorized()){return Flowable.error(new NotAuthorizedException("Not authorized"));}

        return getTransactionsBatch(offset,limit);
    }

    private int count = Integer.MAX_VALUE;

    @MainThread
    public int getCount() {
        return count;
    }

    @MainThread
    private void setCount(int count){
        this.count = count;
    }

    private Flowable<ChatList.ChatDescription> getTransactionsBatch(int offset, int limit) {
        Flowable<ChatList> transactionFlowable = null;
        transactionFlowable = api.getChatsByOffset(offset, limit, AdamantApi.ORDER_BY_TIMESTAMP_DESC);

        return transactionFlowable
                .observeOn(AndroidSchedulers.mainThread())
                .map(transactionList->{
                    if (transactionList.isSuccess()) {
                        setCount(transactionList.getCount());
                    }
                    return transactionList;
                })
                .observeOn(Schedulers.io())
                .flatMap(transactionList -> {
                    if (transactionList.isSuccess()) {
                        int count = transactionList.getCount();
                        int newOffset = offset + AdamantApi.MAX_TRANSACTIONS_PER_REQUEST;

                        Flowable<ChatList.ChatDescription> result = Flowable
                                .fromIterable(transactionList.getChats());

                        if (newOffset <= count) {
                            return result.concatWith(getTransactionsBatch(newOffset,limit));
                        }

                        return result;
                    } else {
                        return Flowable.error(new Exception(transactionList.getError()));
                    }
                })
                .doOnError(error -> LoggerHelper.e(getClass().getSimpleName(), error.getMessage(), error))
                .retryWhen(throwableFlowable -> throwableFlowable.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS, TimeUnit.SECONDS));
    }
}
