package im.adamant.android.helpers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.goterl.lazycode.lazysodium.utils.KeyPair;

import java.lang.reflect.Type;
import java.util.HashMap;

import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.Encryptor;
import im.adamant.android.core.entities.Account;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.TransactionState;
import im.adamant.android.core.entities.transaction_assets.TransactionStateAsset;
import im.adamant.android.core.exceptions.InvalidValueForKeyValueStorage;
import im.adamant.android.ui.entities.Contact;
import io.reactivex.Flowable;

public class KvsHelper {
    private AdamantApiWrapper api;
    private Encryptor encryptor;
    private Gson gson;

    public KvsHelper(AdamantApiWrapper api, Encryptor encryptor, Gson gson) {
        this.api = api;
        this.encryptor = encryptor;
        this.gson = gson;
    }

    public <T> Transaction<TransactionStateAsset> transformToTransaction(String key, boolean encrypted, T object) {
        JsonObject valueObject = new JsonObject();
        valueObject.add("payload", gson.toJsonTree(object));
        String valueString = valueObject.toString();

        KeyPair keyPair = api.getKeyPair();
        Account account = api.getAccount();

        if (keyPair == null || account == null){return null;}

        Transaction<TransactionStateAsset> transaction = new Transaction<>();
        transaction.setType(Transaction.STATE);
        transaction.setSenderId(account.getAddress());
        transaction.setSenderPublicKey(keyPair.getPublicKeyString().toLowerCase());
        transaction.setTimestamp(api.getEpoch() - api.getServerTimeDelta());

        TransactionState state = null;
        if (encrypted){
            state = encryptor.encryptState(key, valueString, keyPair.getSecretKeyString());
        } else {
            state = new TransactionState();
            state.setKey(key);
            state.setType(0);
            state.setValue(valueString);
        }

        TransactionStateAsset asset = new TransactionStateAsset();
        asset.setState(state);

        transaction.setAsset(asset);
        String sign = encryptor.createTransactionSignature(transaction, keyPair);
        transaction.setSignature(sign);

        return transaction;
    }

    public <T> T transformFromTransaction(
            boolean encrypted,
            Transaction<TransactionStateAsset> transaction,
            Type type
    ) throws Exception {
        if (transaction.getType() != Transaction.STATE) {throw new InvalidValueForKeyValueStorage();}

        TransactionStateAsset asset = transaction.getAsset();
        if (asset == null) {throw new InvalidValueForKeyValueStorage();}

        TransactionState state = asset.getState();
        if (state == null) {throw new InvalidValueForKeyValueStorage();}

        String decryptedStateString = "";
        if (encrypted){
            KeyPair keyPair = api.getKeyPair();
            decryptedStateString = encryptor.decryptState(state, keyPair.getSecretKeyString());
        } else {
            decryptedStateString = state.getValue();
        }

        return gson.fromJson(decryptedStateString, type);

    }
}
