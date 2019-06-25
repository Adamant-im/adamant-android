package im.adamant.android.interactors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import im.adamant.android.BuildConfig;
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
        return Flowable.defer(() -> {
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

            return Flowable.just(list);
        })
        .subscribeOn(Schedulers.computation())
        .timeout(BuildConfig.DEFAULT_OPERATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    public Single<List<CurrencyTransferEntity>> getLastTransfersByCurrencyAbbr(String abbreviation) {
        try {
            SupportedWalletFacadeType supportedCurrencyType = SupportedWalletFacadeType.valueOf(abbreviation);
            if (wallets.containsKey(supportedCurrencyType)){
                WalletFacade facade = wallets.get(supportedCurrencyType);
                if (facade == null){return Single.error(new NullPointerException());}

                return facade.getLastTransfers();
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return Single.error(new Exception("Not found currency facade"));
    }

    public Flowable<CurrencyTransferEntity> getNewTransfersByCurrencyAbbr(String abbreviation) {
        try {
            SupportedWalletFacadeType supportedCurrencyType = SupportedWalletFacadeType.valueOf(abbreviation);
            if (wallets.containsKey(supportedCurrencyType)){
                WalletFacade facade = wallets.get(supportedCurrencyType);
                if (facade == null){return Flowable.error(new NullPointerException());}

                return facade.getNewTransfers();
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return Flowable.error(new Exception("Not found currency facade"));
    }

    public Flowable<CurrencyTransferEntity> getNextTransfersByCurrencyAbbr(String abbreviation, int offset) {
        try {
            SupportedWalletFacadeType supportedCurrencyType = SupportedWalletFacadeType.valueOf(abbreviation);
            if (wallets.containsKey(supportedCurrencyType)){
                WalletFacade facade = wallets.get(supportedCurrencyType);
                if (facade == null){return Flowable.error(new NullPointerException());}

                return facade.getNextTransfers(offset);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return Flowable.error(new Exception("Not found currency facade"));
    }
}
