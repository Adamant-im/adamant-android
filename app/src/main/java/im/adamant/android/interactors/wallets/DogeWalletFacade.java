package im.adamant.android.interactors.wallets;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.wallet.Wallet;
import org.libdohj.params.DogecoinMainNetParams;
import org.spongycastle.crypto.ec.ECPair;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.Hex;
import im.adamant.android.helpers.ChatsStorage;
import im.adamant.android.ui.entities.CurrencyTransferEntity;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class DogeWalletFacade implements WalletFacade {
    private AdamantApiWrapper api;

    public DogeWalletFacade(AdamantApiWrapper api) {
        this.api = api;
    }

    @Override
    public BigDecimal getBalance() {
        return BigDecimal.ZERO;
    }

    @Override
    public String getAddress() {
        DogecoinMainNetParams params = new DogecoinMainNetParams();
        ECKey keyPairDodge = api.getKeyPairDodge();
        Address address = keyPairDodge.toAddress(params);

        return address.toString();
    }

    @Override
    public SupportedWalletFacadeType getCurrencyType() {
        return SupportedWalletFacadeType.DOGE;
    }

    @Override
    public String getTitle() {
        return "DOGE WALLET";
    }

    @Override
    public int getPrecision() {
        return 8;
    }

    @Override
    public int getBackgroundLogoResource() {
        return 0;
    }

    @Override
    public void setChatStorage(ChatsStorage chatStorage) {

    }

    @Override
    public Single<List<CurrencyTransferEntity>> getLastTransfers() {
        return Single.just(new ArrayList<>());
    }

    @Override
    public boolean isAvailableAirdropLink() {
        return false;
    }

    @Override
    public int getAirdropLinkResource() {
        return 0;
    }

    @Override
    public String getAirdropLinkString() {
        return null;
    }

    @Override
    public boolean isSupportFundsSending() {
        return false;
    }

    @Override
    public Flowable<BigDecimal> getFee() {
        return Flowable.just(BigDecimal.ZERO);
    }

    @Override
    public String getCurrencyAddress(String adamantAddress, String adamantPublicKey) {
        return null;
    }

    @Override
    public int getIconForEditText() {
        return 0;
    }

    @Override
    public boolean isSupportComment() {
        return false;
    }
}
