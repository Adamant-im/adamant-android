package com.dremanovich.adamant_android.interactors;

import com.dremanovich.adamant_android.core.AdamantApi;
import com.dremanovich.adamant_android.core.encryption.KeyGenerator;
import com.dremanovich.adamant_android.core.helpers.interfaces.AuthorizationStorage;
import com.dremanovich.adamant_android.core.responses.Authorization;
import com.goterl.lazycode.lazysodium.utils.KeyPair;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class AuthorizeInteractor {
    private AdamantApi api;
    private AuthorizationStorage storage;
    private KeyGenerator keyGenerator;

    public AuthorizeInteractor(
            AdamantApi api,
            AuthorizationStorage storage,
            KeyGenerator keyGenerator
    ) {
        this.api = api;
        this.storage = storage;
        this.keyGenerator = keyGenerator;
    }

    public Observable<Authorization> authorize(String passPhrase){
        try {
            KeyPair keyPair = keyGenerator.getKeyPairFromPassPhrase(passPhrase);
            return api.authorize(keyPair.getPublicKeyString().toLowerCase())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext((authorization -> {
                        if (authorization.isSuccess()){
                            storage.setAuth(authorization.getAccount(), keyPair);
                        }
                    }));
        }catch (Exception ex){
            ex.printStackTrace();
            return Observable.error(ex);
        }

    }
}
