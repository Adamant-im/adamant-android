package im.adamant.android.interactors;

import java.math.BigDecimal;
import java.math.RoundingMode;

import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.helpers.interfaces.AuthorizationStorage;

public class AccountInteractor {
    private static final BigDecimal HUNDRED_MILLION = new BigDecimal(100_000_000L);
    private AdamantApiWrapper api;

    public AccountInteractor(AdamantApiWrapper api) {
        this.api = api;
    }

    public String getAdamantAddress() {
        String address = "";
        if (api.isAuthorized()){
            address = api.getAccount().getAddress();
        }

        return address;
    }

    public BigDecimal getAdamantBalance() {
        BigDecimal balance = BigDecimal.ZERO;
        if (api.isAuthorized()){
            balance = (new BigDecimal(
                            api.getAccount().getBalance()
                    ))
                    .divide(
                            HUNDRED_MILLION,
                            3,
                            RoundingMode.HALF_EVEN
                    );
        }
        return balance;
    }

    public void logout() {
        api.logout();
    }
}
