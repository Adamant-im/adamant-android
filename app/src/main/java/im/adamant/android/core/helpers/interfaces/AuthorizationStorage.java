package im.adamant.android.core.helpers.interfaces;

import im.adamant.android.core.entities.Account;
import com.goterl.lazycode.lazysodium.utils.KeyPair;


public interface AuthorizationStorage {
    void setAuth(Account account, KeyPair keyPair);
    Account getAccount();
    KeyPair getKeyPair();
    boolean isAuth();
}
