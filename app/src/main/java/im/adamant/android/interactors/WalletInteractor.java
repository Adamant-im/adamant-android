package im.adamant.android.interactors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
import im.adamant.android.interactors.wallets.WalletFacade;
import im.adamant.android.ui.entities.CurrencyCardItem;
import im.adamant.android.ui.entities.CurrencyTransferEntity;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class WalletInteractor {
    private Map<SupportedWalletFacadeType, WalletFacade> wallets;

    public WalletInteractor(Map<SupportedWalletFacadeType, WalletFacade> wallets) {
        this.wallets = wallets;
    }

    public Flowable<List<CurrencyCardItem>> getCurrencyItemCards() {
        return Flowable.fromCallable(() -> {
            List<CurrencyCardItem> list = new ArrayList<>();

            for (SupportedWalletFacadeType currencyType : SupportedWalletFacadeType.values()){
                if (wallets.containsKey(currencyType)){
                    WalletFacade wallet = wallets.get(currencyType);
                    if (wallet == null){continue;}

                    CurrencyCardItem item = new CurrencyCardItem();
                    item.setAddress(wallet.getAddress());
                    item.setBalance(wallet.getBalance());
                    item.setTitleString(wallet.getTitle());
                    item.setPrecision(wallet.getPrecision());
                    item.setBackgroundLogoResource(wallet.getBackgroundLogoResource());
                    item.setAbbreviation(currencyType.name());
                    if (wallet.isAvailableAirdropLink()){
                        item.setAirdropLinkResource(wallet.getAirdropLinkResource());
                    }
                    list.add(item);
                }

            }

            return list;
        })
                .subscribeOn(Schedulers.computation());
    }

    public Single<List<CurrencyTransferEntity>> getLastTransfersByCurrencyAbbr(String abbreviation) {
        try {
            SupportedWalletFacadeType supportedCurrencyType = SupportedWalletFacadeType.valueOf(abbreviation);
            if (wallets.containsKey(supportedCurrencyType)){
                WalletFacade driver = wallets.get(supportedCurrencyType);
                if (driver == null){return Single.error(new NullPointerException());}

                return driver.getLastTransfers();
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return Single.error(new Exception("Not found currency info driver"));
    }
}
