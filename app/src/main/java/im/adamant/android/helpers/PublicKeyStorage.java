package im.adamant.android.helpers;

import android.util.Pair;

import im.adamant.android.core.entities.Transaction;

public interface PublicKeyStorage {
    String getPublicKey(String address);
    void setPublicKey(String address, String publicKey);
    Pair<String, Transaction<?>> combinePublicKeyWithTransaction(Transaction<?> transaction) throws Exception;
}
