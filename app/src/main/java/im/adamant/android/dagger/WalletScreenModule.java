package im.adamant.android.dagger;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Screens;
import im.adamant.android.helpers.QrCodeHelper;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.RefreshChatsInteractor;
import im.adamant.android.presenters.WalletPresenter;
import im.adamant.android.ui.adapters.CurrencyCardAdapter;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

@Module
public class WalletScreenModule {
    @FragmentScope
    @Provides
    public WalletPresenter provideWalletPresenter(
            Router router,
            AccountInteractor accountInteractor,
            RefreshChatsInteractor refreshChatsInteractor,
            @Named(value = Screens.WALLET_SCREEN) CompositeDisposable subscriptions
    ){
        return new WalletPresenter(router, accountInteractor, refreshChatsInteractor, subscriptions);
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
}
