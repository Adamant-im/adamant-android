package com.dremanovich.adamant_android.core.helpers;

import com.dremanovich.adamant_android.core.AdamantApi;
import com.dremanovich.adamant_android.core.helpers.interfaces.PublicKeyStorage;
import com.dremanovich.adamant_android.core.responses.PublicKeyResponse;

import java.util.HashMap;

public class NaivePublicKeyStorageImpl implements PublicKeyStorage {
    private HashMap<String, String> publicKeys = new HashMap<>();
    private AdamantApi api;

    public NaivePublicKeyStorageImpl(AdamantApi api) {
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
