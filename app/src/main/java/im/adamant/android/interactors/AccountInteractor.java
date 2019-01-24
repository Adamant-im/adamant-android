package im.adamant.android.interactors;

import java.math.BigDecimal;

import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.helpers.BalanceConvertHelper;
import im.adamant.android.helpers.ChatsStorage;
import im.adamant.android.helpers.Settings;
import io.reactivex.Flowable;

public class AccountInteractor {
    private AdamantApiWrapper api;
    private Settings settings;
    private ChatsStorage chatsStorage;

    public AccountInteractor(
            AdamantApiWrapper api,
            Settings settings,
            ChatsStorage chatsStorage
    ) {
        this.api = api;
        this.settings = settings;
        this.chatsStorage = chatsStorage;
    }

    public Flowable<BigDecimal> getAdamantBalance() {
        return Flowable.fromCallable(this::getBalance);
    }

    private BigDecimal getBalance() {
        if (api.isAuthorized()){
            return BalanceConvertHelper.convert(api.getAccount().getUnconfirmedBalance());
        } else {
            return BigDecimal.ZERO;
        }
    }
}
