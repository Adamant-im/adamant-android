package im.adamant.android.dagger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.helpers.ChatsStorage;
import im.adamant.android.interactors.wallets.AdamantWalletFacade;
import im.adamant.android.interactors.wallets.BinanceWalletFacade;
import im.adamant.android.interactors.wallets.EthereumWalletFacade;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeTypeKey;
import im.adamant.android.interactors.wallets.WalletFacade;

@Module
public abstract class WalletsModule {
    @IntoMap
    @SupportedWalletFacadeTypeKey(SupportedWalletFacadeType.ADM)
    @Singleton
    @Provides
    public static WalletFacade provideAdamantInfoDriver(AdamantApiWrapper api, ChatsStorage chatStorage) {
        AdamantWalletFacade driver = new AdamantWalletFacade(api);
        driver.setChatStorage(chatStorage);

        return driver;
    }

    //TODO: Don't forget inject ChatStorage
    @IntoMap
    @SupportedWalletFacadeTypeKey(SupportedWalletFacadeType.ETH)
    @Singleton
    @Provides
    public static WalletFacade provideEthereumInfoDriver() {
        return new EthereumWalletFacade();
    }

    @IntoMap
    @SupportedWalletFacadeTypeKey(SupportedWalletFacadeType.BNB)
    @Singleton
    @Provides
    public static WalletFacade provideBinanceInfoDriver() {
        return new BinanceWalletFacade();
    }
}
