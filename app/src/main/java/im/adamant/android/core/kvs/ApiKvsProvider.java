package im.adamant.android.core.kvs;

import java.util.ArrayList;
import java.util.List;

import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.transaction_assets.TransactionStateAsset;
import im.adamant.android.core.exceptions.EmptyAdamantKeyValueStorage;
import io.reactivex.Completable;
import io.reactivex.Flowable;

public class ApiKvsProvider implements KvsProvider {
    private AdamantApiWrapper api;

    public ApiKvsProvider(AdamantApiWrapper api) {
        this.api = api;
    }

    @Override
    public Flowable<Transaction<TransactionStateAsset>> get(String key) {
        if (!api.isAuthorized()){return Flowable.error(new Exception("Not authorized"));}
        String ownerId = api.getAccount().getAddress();

        return get(key, ownerId);
    }

    @Override
    public Flowable<Transaction<TransactionStateAsset>> get(String key, String ownerAddress) {
        return api.getFromKeyValueStorage(
            ownerAddress,
            key,
            AdamantApi.ORDER_BY_TIMESTAMP_DESC,
            1
        )
        .flatMap(transactionList -> {
            if(transactionList.isSuccess()){
                return Flowable.just(transactionList.getTransactions());
            } else {
                return Flowable.error(new Exception(transactionList.getError()));
            }
        })
        .flatMap(transactions -> {
            if (transactions.size() > 0) {
                return Flowable.just(transactions.get(0));
            } else {
                return Flowable.error(new EmptyAdamantKeyValueStorage());
            }
        })
        .onErrorReturn(error -> new Transaction<>());
    }

    @Override
    public Completable put(Transaction<TransactionStateAsset> transaction) {
        //TODO: "Retry" shouldn't be infinite!
        return  api
                .sendToKeyValueStorage(transaction)
                .retry()
                .ignoreElements();
    }
}
