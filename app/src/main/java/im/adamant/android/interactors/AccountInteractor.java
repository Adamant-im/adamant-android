package im.adamant.android.interactors;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import im.adamant.android.R;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.KeyStoreCipher;
import im.adamant.android.currencies.CurrencyInfoDriver;
import im.adamant.android.currencies.CurrencyTransferEntity;
import im.adamant.android.currencies.SupportedCurrencyType;
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
    private Map<SupportedCurrencyType, CurrencyInfoDriver> infoDrivers;

    public AccountInteractor(
            AdamantApiWrapper api,
            Settings settings,
            ChatsStorage chatsStorage,
            Map<SupportedCurrencyType, CurrencyInfoDriver> infoDrivers
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

    public Flowable<List<CurrencyCardItem>> getCurrencyItemCards() {
        return Flowable.fromCallable(() -> {
                    List<CurrencyCardItem> list = new ArrayList<>();

                    for (SupportedCurrencyType currencyType : SupportedCurrencyType.values()){
                        if (infoDrivers.containsKey(currencyType)){
                            CurrencyInfoDriver driver = infoDrivers.get(currencyType);
                            if (driver == null){continue;}

                            CurrencyCardItem item = new CurrencyCardItem();
                            item.setAddress(driver.getAddress());
                            item.setBalance(driver.getBalance());
                            item.setTitleString(driver.getTitle());
                            item.setPrecision(driver.getPrecision());
                            item.setBackgroundLogoResource(driver.getBackgroundLogoResource());
                            item.setAbbreviation(currencyType.name());
                            list.add(item);
                        }

                    }

                    return list;
                })
                .subscribeOn(Schedulers.computation());
    }

    public Single<List<CurrencyTransferEntity>> getLastTransfersByCurrencyAbbr(String abbreviation) {
        try {
            SupportedCurrencyType supportedCurrencyType = SupportedCurrencyType.valueOf(abbreviation);
            if (infoDrivers.containsKey(supportedCurrencyType)){
                CurrencyInfoDriver driver = infoDrivers.get(supportedCurrencyType);
                if (driver == null){return Single.error(new NullPointerException());}

                return driver.getLastTransfers();
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return Single.error(new Exception("Not found currency info driver"));
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
