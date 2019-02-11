package im.adamant.android.dagger.fragments;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Screens;
import im.adamant.android.helpers.QrCodeHelper;
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
    public WalletPresenter provideWalletPresenter(
            Router router,
            WalletInteractor walletInteractor,
            @Named(value = Screens.WALLET_SCREEN) CompositeDisposable subscriptions
    ){
        return new WalletPresenter(router, walletInteractor, subscriptions);
    }

    @FragmentScope
    @Provides
    @Named(value = Screens.WALLET_SCREEN)
    public QrCodeHelper provideQrCodeParser() {
        return new QrCodeHelper();
    }

    @FragmentScope
    @Provides
    @Named(value = Screens.WALLET_SCREEN)
    public CompositeDisposable provideComposite() {
        return new CompositeDisposable();
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
}
