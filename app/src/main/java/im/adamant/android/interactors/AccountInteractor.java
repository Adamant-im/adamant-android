package im.adamant.android.interactors;

import java.math.BigDecimal;
import java.math.RoundingMode;

import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.KeyStoreCipher;
import im.adamant.android.helpers.Settings;
import io.reactivex.Flowable;

public class AccountInteractor {
    private static final BigDecimal HUNDRED_MILLION = new BigDecimal(100_000_000L);
    private AdamantApiWrapper api;
    private Settings settings;

    public AccountInteractor(AdamantApiWrapper api, Settings settings) {
        this.api = api;
        this.settings = settings;
    }

    public String getAdamantAddress() {
        String address = "";
        if (api.isAuthorized()){
            address = api.getAccount().getAddress();
        }

        return address;
    }

    public Flowable<BigDecimal> getAdamantBalance() {
        return Flowable.fromCallable(() -> {
            if (api.isAuthorized()){
                return (new BigDecimal(
                        api.getAccount().getUnconfirmedBalance()
                ))
                .divide(
                        HUNDRED_MILLION,
                        3,
                        RoundingMode.HALF_EVEN
                );
            } else {
                return BigDecimal.ZERO;
            }
        });
    }

    public void logout() {
        settings.setAccountKeypair("");
        api.logout();
    }
}
