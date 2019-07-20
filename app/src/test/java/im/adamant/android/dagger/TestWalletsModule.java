package im.adamant.android.dagger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import im.adamant.android.interactors.wallets.AdamantWalletFacade;
import im.adamant.android.interactors.wallets.BinanceWalletFacade;
import im.adamant.android.interactors.wallets.EthereumWalletFacade;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeTypeKey;
import im.adamant.android.interactors.wallets.WalletFacade;

import static org.mockito.Mockito.mock;

@Module
public abstract class TestWalletsModule {
    @IntoMap
    @SupportedWalletFacadeTypeKey(SupportedWalletFacadeType.ADM)
    @Singleton
    @Provides
    public static WalletFacade provideAdamantInfoDriver() {
        return mock(AdamantWalletFacade.class);
    }

    @IntoMap
    @SupportedWalletFacadeTypeKey(SupportedWalletFacadeType.ETH)
    @Singleton
    @Provides
    public static WalletFacade provideEthereumInfoDriver() {
        return mock(EthereumWalletFacade.class);
    }

    @IntoMap
    @SupportedWalletFacadeTypeKey(SupportedWalletFacadeType.BNB)
    @Singleton
    @Provides
    public static WalletFacade provideBinanceInfoDriver() {
        return mock(BinanceWalletFacade.class);
    }
}
