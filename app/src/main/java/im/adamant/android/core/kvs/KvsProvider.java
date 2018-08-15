package im.adamant.android.core.kvs;

import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.TransactionState;
import im.adamant.android.core.entities.transaction_assets.TransactionStateAsset;
import io.reactivex.Completable;
import io.reactivex.Flowable;

public interface KvsProvider {
    Flowable<Transaction<TransactionStateAsset>> get(String key);
    Flowable<Transaction<TransactionStateAsset>> get(String key, String ownerAddress);
    Completable put(String key, Transaction<TransactionStateAsset> transaction);
}
