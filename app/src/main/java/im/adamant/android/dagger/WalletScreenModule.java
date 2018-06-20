package im.adamant.android.dagger;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.presenters.WalletPresenter;
import ru.terrakok.cicerone.Router;

@Module
public class WalletScreenModule {
    @FragmentScope
    @Provides
    public WalletPresenter provideWalletPresenter(
            Router router,
            AccountInteractor interactor
    ){
        return new WalletPresenter(router, interactor);
    }
}
