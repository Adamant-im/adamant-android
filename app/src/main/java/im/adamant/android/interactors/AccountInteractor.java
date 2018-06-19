package im.adamant.android.interactors;

import java.math.BigDecimal;
import java.math.RoundingMode;

import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.helpers.interfaces.AuthorizationStorage;

public class AccountInteractor {
    private static final BigDecimal HUNDRED_MILLION = new BigDecimal(100_000_000L);
    private AuthorizationStorage storage;

    public AccountInteractor(AuthorizationStorage storage) {
        this.storage = storage;
    }

    public String getAdamantAddress() {
        String address = "";
        if (storage.isAuth()){
            address = storage.getAccount().getAddress();
        }

        return address;
    }

    public BigDecimal getAdamantBalance() {
        BigDecimal balance = BigDecimal.ZERO;
        if (storage.isAuth()){
            balance = (new BigDecimal(storage.getAccount().getBalance())).divide(HUNDRED_MILLION, 3, RoundingMode.HALF_EVEN);
        }
        return balance;
    }
}
