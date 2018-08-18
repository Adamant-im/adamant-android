package im.adamant.android.interactors;

import java.math.BigDecimal;
import java.math.RoundingMode;

import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.KeyStoreCipher;
import im.adamant.android.helpers.BalanceConvertHelper;
import im.adamant.android.helpers.ChatsStorage;
import im.adamant.android.helpers.Settings;
import io.reactivex.Flowable;

public class AccountInteractor {
    private AdamantApiWrapper api;
    private Settings settings;
    private ChatsStorage chatsStorage;

    public AccountInteractor(AdamantApiWrapper api, Settings settings, ChatsStorage chatsStorage) {
        this.api = api;
        this.settings = settings;
        this.chatsStorage = chatsStorage;
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
                return BalanceConvertHelper.convert(api.getAccount().getUnconfirmedBalance());
            } else {
                return BigDecimal.ZERO;
            }
        });
    }

    public void logout() {
        settings.setAccountKeypair("");
        chatsStorage.cleanUp();
        api.logout();
    }
}
