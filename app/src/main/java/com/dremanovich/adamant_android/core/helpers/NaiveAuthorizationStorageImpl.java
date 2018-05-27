package com.dremanovich.adamant_android.core.helpers;

import com.dremanovich.adamant_android.core.entities.Account;
import com.dremanovich.adamant_android.core.helpers.interfaces.AuthorizationStorage;
import com.goterl.lazycode.lazysodium.utils.KeyPair;


public class NaiveAuthorizationStorageImpl implements AuthorizationStorage {
    private Account account;
    private KeyPair keyPair;

    @Override
    public void setAuth(Account account, KeyPair keyPair) {
        this.account = account;
        this.keyPair = keyPair;
    }

    @Override
    public Account getAccount() {
        return account;
    }

    @Override
    public KeyPair getKeyPair() {
        return keyPair;
    }

    @Override
    public boolean isAuth() {
        return account != null && keyPair != null;
    }
}
