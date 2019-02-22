package im.adamant.android.dagger.fragments;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Screens;
import im.adamant.android.helpers.QrCodeHelper;
import im.adamant.android.ui.adapters.CurrencyCardAdapter;
import im.adamant.android.ui.adapters.CurrencyTransfersAdapter;

import static org.mockito.Mockito.mock;

@Module
public class TestWalletScreenModule {
    @FragmentScope
    @Provides
    @Named(value = Screens.WALLET_SCREEN)
    public QrCodeHelper provideQrCodeParser() {
        return mock(QrCodeHelper.class);
    }

    @FragmentScope
    @Provides
    public CurrencyCardAdapter provideCardAdapter() {
        return mock(CurrencyCardAdapter.class);
    }

    @FragmentScope
    @Provides
    public CurrencyTransfersAdapter provideCurrencyTransferAdapter() {
        return mock(CurrencyTransfersAdapter.class);
    }
}
