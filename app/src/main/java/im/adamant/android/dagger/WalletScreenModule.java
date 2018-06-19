package im.adamant.android.dagger;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.presenters.WalletPresenter;

@Module
public class WalletScreenModule {
    @FragmentScope
    @Provides
    public WalletPresenter provideWalletPresenter(
            AccountInteractor interactor
    ){
        return new WalletPresenter(interactor);
    }
}
