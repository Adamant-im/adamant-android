package im.adamant.android.helpers;

import android.util.Pair;

import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.entities.Participant;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.core.responses.PublicKeyResponse;
import io.reactivex.exceptions.UndeliverableException;

import java.util.HashMap;

public class NaivePublicKeyStorageImpl implements PublicKeyStorage {
    private HashMap<String, String> publicKeys = new HashMap<>();
    private AdamantApiWrapper api;
    private String ownAddress = "";

    public NaivePublicKeyStorageImpl(AdamantApiWrapper api) {
        this.api = api;
    }

    //TODO: Return Flowable and you may use zip operator.
    @Override
    public String getPublicKey(String address) {
        if (!publicKeys.containsKey(address)){
            try {
                //TODO: InterrupedException. Application Crashed.
                PublicKeyResponse response = api.getPublicKey(address).blockingFirst();
                if (response.isSuccess()){
                    publicKeys.put(address, response.getPublicKey());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
        return publicKeys.containsKey(address) ? publicKeys.get(address) : "";
    }

    @Override
    public void setPublicKey(String address, String publicKey) {
        publicKeys.put(address, publicKey);
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

        if (pKey == null) {
            pKey = getPublicKey(address, transaction);
            if (!pKey.isEmpty()) {
                publicKeys.put(address, pKey);
            }
        }

        return new Pair<>(pKey, transaction);
    }

    private String getPublicKey(String address, Transaction<?> transaction) {
        if (transaction.getParticipants() != null) {
            for(Participant participant : transaction.getParticipants()) {
                if (address.equalsIgnoreCase(participant.getAddress()) && participant.getPublicKey() != null) {
                    return participant.getPublicKey();
                }
            }
        }

        return "";
    }
}
