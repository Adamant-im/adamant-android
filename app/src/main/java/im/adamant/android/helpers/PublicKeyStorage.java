package im.adamant.android.helpers;

import android.util.Pair;

import im.adamant.android.core.entities.Transaction;
import io.reactivex.Flowable;

public interface PublicKeyStorage {
    void setPublicKey(String address, String publicKey);
    Flowable<String> findPublicKey(String address);
    Pair<String, Transaction<?>> combinePublicKeyWithTransaction(Transaction<?> transaction) throws Exception;
}
