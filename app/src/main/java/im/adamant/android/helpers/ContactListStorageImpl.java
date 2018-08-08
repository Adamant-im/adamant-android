package im.adamant.android.helpers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.goterl.lazycode.lazysodium.utils.KeyPair;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.Encryptor;
import im.adamant.android.core.entities.Account;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.TransactionState;
import im.adamant.android.core.entities.transaction_assets.TransactionAsset;
import im.adamant.android.core.entities.transaction_assets.TransactionStateAsset;
import im.adamant.android.core.exceptions.EmptyAdamantKeyValueStorage;
import im.adamant.android.core.exceptions.InvalidValueForKeyValueStorage;
import im.adamant.android.ui.entities.Contact;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

//TODO: This code must be in chats interactor
public class ContactListStorageImpl implements Closeable {
    private AdamantApiWrapper api;
    private Encryptor encryptor;
    private CompositeDisposable subscriptions = new CompositeDisposable();

    private final Gson gson = new Gson();

    public ContactListStorageImpl(AdamantApiWrapper api, Encryptor encryptor) {
        this.api = api;
        this.encryptor = encryptor;
    }

    public void put(String key, List<Contact> value) {
        String valueString = gson.toJson(value);
        Transaction<TransactionStateAsset> transaction = buildTransaction(key, valueString);
        Disposable subscribe = api
                .sendToKeyValueStorage(transaction)
                .retry()
                .subscribe();
        subscriptions.add(subscribe);
    }

    public Flowable<HashMap<String, Contact>> get(String key) {
        if (!api.isAuthorized()){return Flowable.error(new Exception("Not authorized"));}
        String ownerId = api.getAccount().getAddress();

        return get(key, ownerId);
    }

    public Flowable<HashMap<String, Contact>> get(String key, String ownerId) {
        return
        api.getFromKeyValueStorage(
                ownerId,
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
            .flatMap(transaction -> {
                if (transaction.getType() != Transaction.STATE) {return Flowable.error(new InvalidValueForKeyValueStorage());}

                TransactionStateAsset asset = (TransactionStateAsset) transaction.getAsset();
                if (asset == null) {return Flowable.error(new InvalidValueForKeyValueStorage());}

                TransactionState state = asset.getState();
                if (state == null) {return Flowable.error(new InvalidValueForKeyValueStorage());}

                try {
                    KeyPair keyPair = api.getKeyPair();

                    String decryptedStateString = encryptor.decryptState(state, keyPair.getSecretKeyString());

                    HashMap<String, Contact> contacts = gson.fromJson(decryptedStateString, new TypeToken<HashMap<String, Contact>>() {}.getType());

                    return Flowable.just(contacts);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return Flowable.error(ex);
                }
            });
    }

    private Transaction<TransactionStateAsset> buildTransaction(String key, String value){

        KeyPair keyPair = api.getKeyPair();
        Account account = api.getAccount();

        if (keyPair == null || account == null){return null;}

        Transaction<TransactionStateAsset> transaction = new Transaction<>();
        transaction.setType(Transaction.STATE);
        transaction.setSenderId(account.getAddress());
        transaction.setSenderPublicKey(keyPair.getPublicKeyString().toLowerCase());
        transaction.setTimestamp(api.getEpoch() - api.getServerTimeDelta());

        TransactionState state = encryptor.encryptState(key, value, keyPair.getSecretKeyString());

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
        subscriptions.clear();
    }
}
