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

    public Flowable<SendCurrencyEntity> getAvailableCurrencies(String recipientAddress) {
        return Flowable
                .fromArray(SupportedWalletFacadeType.values())
                .flatMap(type -> {
                    if (wallets.containsKey(type)) {
                        WalletFacade walletFacade = wallets.get(type);

                        return walletFacade == null ?
                                Flowable.error(new NotSupportedWalletFacade("Not supported wallet: " + type)) :
                                Flowable.just(walletFacade);
                    } else {
                        return Flowable.error(new NotSupportedWalletFacade("Not supported wallet: " + type));
                    }
                })
                .flatMap(walletFacade -> {
                    String pKey = publicKeyStorage.getPublicKey(recipientAddress);
                    SendCurrencyEntity sendCurrencyEntity = walletFacade.getSendCurrencyEntity(recipientAddress, pKey);
                    return Flowable.just(sendCurrencyEntity);
                });
    }
}
