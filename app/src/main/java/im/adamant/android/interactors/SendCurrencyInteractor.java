package im.adamant.android.interactors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import im.adamant.android.core.exceptions.NotSupportedWalletFacade;
import im.adamant.android.helpers.PublicKeyStorage;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
import im.adamant.android.interactors.wallets.WalletFacade;
import im.adamant.android.ui.entities.SendCurrencyEntity;
import io.reactivex.Flowable;

public class SendCurrencyInteractor {
    private Map<SupportedWalletFacadeType, WalletFacade> wallets;
    private PublicKeyStorage publicKeyStorage;

    public SendCurrencyInteractor(
            Map<SupportedWalletFacadeType, WalletFacade> wallets,
            PublicKeyStorage publicKeyStorage
    ) {
        this.wallets = wallets;
        this.publicKeyStorage = publicKeyStorage;
    }

    public WalletFacade getFacade(SupportedWalletFacadeType type) {
        return wallets.get(type);
    }
}
