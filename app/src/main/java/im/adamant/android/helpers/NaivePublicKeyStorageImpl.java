package im.adamant.android.helpers;

import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.responses.PublicKeyResponse;

import java.util.HashMap;

public class NaivePublicKeyStorageImpl implements PublicKeyStorage {
    private HashMap<String, String> publicKeys = new HashMap<>();
    private AdamantApiWrapper api;

    public NaivePublicKeyStorageImpl(AdamantApiWrapper api) {
        this.api = api;
    }

    @Override
    public String getPublicKey(String address) {
        if (!publicKeys.containsKey(address)){
            PublicKeyResponse response = api.getPublicKey(address).blockingFirst();
            if (response.isSuccess()){
                publicKeys.put(address, response.getPublicKey());
            }
        }
        return publicKeys.containsKey(address) ? publicKeys.get(address) : "";
    }
}
