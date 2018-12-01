package im.adamant.android.interactors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import im.adamant.android.R;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
import im.adamant.android.interactors.wallets.WalletFacade;
import im.adamant.android.ui.entities.CurrencyTransferEntity;
import im.adamant.android.helpers.BalanceConvertHelper;
import im.adamant.android.helpers.ChatsStorage;
import im.adamant.android.helpers.Settings;
import im.adamant.android.ui.entities.CurrencyCardItem;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

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


    public void logout() {
        settings.setAccountKeypair("");
        chatsStorage.cleanUp();
        api.logout();
    }

    private BigDecimal getBalance() {
        if (api.isAuthorized()){
            return BalanceConvertHelper.convert(api.getAccount().getUnconfirmedBalance());
        } else {
            return BigDecimal.ZERO;
        }
    }
}
