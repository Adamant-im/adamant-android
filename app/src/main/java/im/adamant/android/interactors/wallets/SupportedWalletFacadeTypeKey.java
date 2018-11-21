package im.adamant.android.interactors.wallets;

import dagger.MapKey;

@MapKey
public @interface SupportedWalletFacadeTypeKey {
    SupportedWalletFacadeType value();
}
