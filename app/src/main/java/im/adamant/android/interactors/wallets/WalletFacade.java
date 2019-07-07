package im.adamant.android.interactors.wallets;

import java.math.BigDecimal;
import java.util.List;

import im.adamant.android.interactors.chats.ChatsStorage;
import im.adamant.android.interactors.wallets.entities.TransferDetails;
import im.adamant.android.ui.entities.CurrencyTransferEntity;
import io.reactivex.Flowable;
import io.reactivex.Single;

//Facade is needed to simplify each blockchain's API for working with a wallet.
public interface WalletFacade {
    BigDecimal getBalance();
    String getAddress();
    SupportedWalletFacadeType getCurrencyType();
    String getTitle();
    int getPrecision();
    int getBackgroundLogoResource();
    void setChatStorage(ChatsStorage chatStorage);
    Single<List<CurrencyTransferEntity>> getLastTransfers();
    Flowable<CurrencyTransferEntity> getNewTransfers();
    Flowable<CurrencyTransferEntity> getNextTransfers(int offset);
    boolean isAvailableAirdropLink();
    int getAirdropLinkResource();
    String getAirdropLinkString();
    boolean isSupportFundsSending();
    Flowable<BigDecimal> getFee();
    String getCurrencyAddress(String adamantAddress, String adamantPublicKey);
    int getIconForEditText();
    boolean isSupportComment();
    Flowable<TransferDetails> getTransferDetails(String id);
    String getExplorerUrl(String transactionId);
}
