package im.adamant.android.interactors;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import im.adamant.android.R;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.KeyStoreCipher;
import im.adamant.android.currencies.CurrencyInfoDriver;
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
    private Set<CurrencyInfoDriver> infoDrivers;

    public AccountInteractor(
            AdamantApiWrapper api,
            Settings settings,
            ChatsStorage chatsStorage,
            Set<CurrencyInfoDriver> infoDrivers
    ) {
        this.api = api;
        this.settings = settings;
        this.chatsStorage = chatsStorage;
        this.infoDrivers = infoDrivers;
    }

    //TODO: Refactor this. Functionality was moved in CurrencyInfoDriver

    public String getAdamantAddress() {
        String address = "";
        if (api.isAuthorized()){
            address = api.getAccount().getAddress();
        }

        return address;
    }

    public Flowable<BigDecimal> getAdamantBalance() {
        return Flowable.fromCallable(this::getBalance);
    }

    public Single<List<CurrencyCardItem>> getCurrencyItemCards() {
        return Single.fromCallable(() -> {
            List<CurrencyCardItem> list = new ArrayList<>();
            for(CurrencyInfoDriver infoDriver : infoDrivers){
                CurrencyCardItem item = new CurrencyCardItem();
                item.setAddress(infoDriver.getAddress());
                item.setBalance(infoDriver.getBalance());
                item.setTitleString(infoDriver.getTitle());
                item.setPrecision(infoDriver.getPrecision());

                list.add(item);
            }

            return list;
        }).subscribeOn(Schedulers.computation());
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
