package im.adamant.android.interactors.wallets;

import java.math.BigDecimal;
import java.util.List;

import im.adamant.android.helpers.ChatsStorage;
import im.adamant.android.ui.entities.CurrencyTransferEntity;
import im.adamant.android.ui.entities.SendCurrencyEntity;
import im.adamant.android.ui.mvp_view.SendCurrencyTransferView;
import io.reactivex.Flowable;
import io.reactivex.Single;

//Facade is needed to simplify each blockchain's API for working with a wallet.
public interface WalletFacade {
    BigDecimal getBalance();
    String getAddress();
    SupportedWalletFacadeType getCurrencyType();
    //TODO: Remove hardcoded values
    String getTitle();
    int getPrecision();
    int getBackgroundLogoResource();
    void setChatStorage(ChatsStorage chatStorage);
    Single<List<CurrencyTransferEntity>> getLastTransfers();
    boolean isAvailableAirdropLink();
    int getAirdropLinkResource();
    String getAirdropLinkString();
    boolean isSupportCurrencySending();
    Flowable<BigDecimal> getFee();
    String getCurrencyAddress(String adamantAddress, String adamantPublicKey);
    int getIconForEditText();
}
