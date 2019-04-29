package im.adamant.android.dagger.fragments;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Screens;
import im.adamant.android.helpers.QrCodeHelper;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.WalletInteractor;
import im.adamant.android.ui.adapters.CurrencyCardAdapter;
import im.adamant.android.ui.adapters.CurrencyTransfersAdapter;
import im.adamant.android.ui.presenters.WalletPresenter;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

@Module
public class WalletScreenModule {
    @FragmentScope
    @Provides
    @Named(value = Screens.WALLET_SCREEN)
    public QrCodeHelper provideQrCodeParser() {
        return new QrCodeHelper();
    }

    @FragmentScope
    @Provides
    public CurrencyCardAdapter provideCardAdapter() {
        return new CurrencyCardAdapter();
    }

    @FragmentScope
    @Provides
    public CurrencyTransfersAdapter provideCurrencyTransferAdapter() {
        return new CurrencyTransfersAdapter();
    }

    @FragmentScope
    @Provides
    public static WalletPresenter provideWalletPresenter(
            Router router,
            AccountInteractor accountInteractor,
            WalletInteractor walletInteractor
    ){
        return new WalletPresenter(router, accountInteractor, walletInteractor);
    }
}
