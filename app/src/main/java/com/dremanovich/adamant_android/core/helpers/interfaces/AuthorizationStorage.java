package com.dremanovich.adamant_android.core.helpers.interfaces;

import com.dremanovich.adamant_android.core.entities.Account;
import com.goterl.lazycode.lazysodium.utils.KeyPair;


public interface AuthorizationStorage {
    void setAuth(Account account, KeyPair keyPair);
    Account getAccount();
    KeyPair getKeyPair();
    boolean isAuth();
}
