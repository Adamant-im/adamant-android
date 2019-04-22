package im.adamant.android.helpers;

import android.util.Pair;

import org.intellij.lang.annotations.Flow;

import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.entities.Participant;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.core.exceptions.NotFoundPublicKey;
import im.adamant.android.core.responses.ChatList;
import im.adamant.android.core.responses.PublicKeyResponse;
import io.reactivex.Flowable;
import io.reactivex.exceptions.UndeliverableException;

import java.util.HashMap;
import java.util.List;

public class PublicKeyStorageImpl implements PublicKeyStorage {
    private HashMap<String, String> publicKeys = new HashMap<>();
    private AdamantApiWrapper api;

    public PublicKeyStorageImpl(AdamantApiWrapper api) {
        this.api = api;
    }

    @Override
    public Flowable<String> findPublicKey(String address) {
        String cachedKey = publicKeys.get(address);

        if (cachedKey == null) {
            return api
                    .getPublicKey(address)
                    .flatMap(response -> {
                        if (response.isSuccess()) {
                            publicKeys.put(address, response.getPublicKey());
                            return Flowable.just(response.getPublicKey());
                        } else  {
                            return Flowable.error(new NotFoundPublicKey("Not oud public key for address: " + address));
                        }
                    });
        } else {
            return Flowable.just(cachedKey);
        }
    }

    @Override
    public void setPublicKey(String address, String publicKey) {
        publicKeys.put(address, publicKey);
    }

    @Override
    public void savePublicKeysFromParticipant(ChatList.ChatDescription description) {
        for (Participant participant : description.getParticipants()) {
            publicKeys.put(participant.getAddress(), participant.getPublicKey());
        }
    }

    @Override
    public Pair<String, Transaction<?>> combinePublicKeyWithTransaction(Transaction<?> transaction) throws Exception {
        if (api.getAccount() == null) { throw new NotAuthorizedException("Not Authorized"); }
        String ownAddress = api.getAccount().getAddress();

        boolean iRecipient = ownAddress.equalsIgnoreCase(transaction.getRecipientId());

        String address = "";
        if (iRecipient) {
            address = transaction.getSenderId();
        } else {
            address = transaction.getRecipientId();
        }

        String pKey = publicKeys.get(address);

        if (pKey == null || pKey.isEmpty()) {
            throw new NotFoundPublicKey("Not oud public key for address: " + address);
        }

        return new Pair<>(pKey, transaction);
    }
}
