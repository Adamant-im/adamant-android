package im.adamant.android.helpers;

import com.google.gson.Gson;
import com.goterl.lazycode.lazysodium.utils.KeyPair;

import java.io.Closeable;
import java.io.IOException;

import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.Encryptor;
import im.adamant.android.core.entities.Account;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.TransactionState;
import im.adamant.android.core.entities.transaction_assets.TransactionStateAsset;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class AdamantKeyValueStorageImpl implements AdamantKeyValueStorage, Closeable {
    private AdamantApiWrapper api;
    private Encryptor encryptor;
    private CompositeDisposable subscriptions = new CompositeDisposable();

    private final Gson gson = new Gson();

    public AdamantKeyValueStorageImpl(AdamantApiWrapper api, Encryptor encryptor) {
        this.api = api;
        this.encryptor = encryptor;
    }

    @Override
    public <T> void put(String key, T value, boolean encrypt) {
        String valueString = gson.toJson(value);
        Transaction<TransactionStateAsset> transaction = buildTransaction(key, valueString, encrypt);
        Disposable subscribe = api
                .sendToKeyValueStorage(transaction)
                .retry()
                .subscribe();
        subscriptions.add(subscribe);
    }

    @Override
    public <T> T get(String key) {
        return null;
    }

    @Override
    public <T> T get(String key, String ownerPublicKey) {
        return null;
    }

    private Transaction<TransactionStateAsset> buildTransaction(String key, String value, boolean encrypt){

        KeyPair keyPair = api.getKeyPair();
        Account account = api.getAccount();

        if (keyPair == null || account == null){return null;}

        Transaction<TransactionStateAsset> transaction = new Transaction<>();
        transaction.setType(Transaction.STATE);
        transaction.setSenderId(account.getAddress());
        transaction.setSenderPublicKey(keyPair.getPublicKeyString().toLowerCase());
        transaction.setTimestamp(api.getEpoch() - api.getServerTimeDelta());

        TransactionState state = null;
        if (encrypt){
            String randomString = encryptor.generateRandomString();
            String paddedPayload = randomString + "{payload: \"" + value + "\"}" + randomString;

            state = encryptor.encryptState(key, paddedPayload, keyPair.getSecretKeyString());
        } else {
            state = new TransactionState();
            state.setValue(value);
            state.setKey(key);
            state.setType(0);
        }


        TransactionStateAsset asset = new TransactionStateAsset();
        asset.setState(state);

        transaction.setAsset(asset);
        String sign = encryptor.createTransactionSignature(transaction, keyPair);
        transaction.setSignSignature(sign);

        return transaction;
    }

    @Override
    public void close() throws IOException {
        subscriptions.dispose();
    }
}
