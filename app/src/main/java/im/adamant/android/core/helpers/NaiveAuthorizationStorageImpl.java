package im.adamant.android.core.helpers;

import im.adamant.android.core.entities.Account;
import im.adamant.android.core.helpers.interfaces.AuthorizationStorage;
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

    @Override
    public void dropAuth() {
        account = null;
        keyPair = null;
    }
}
