package im.adamant.android.dagger.activities;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.WalletInteractor;
import im.adamant.android.ui.presenters.TransferDetailsPresenter;
import ru.terrakok.cicerone.Router;

@Module
public class TransferDetailsScreenModule {
    @ActivityScope
    @Provides
    public TransferDetailsPresenter provideTransferDetailsPresenter(Router router,
                                                                    AccountInteractor accountInteractor,
                                                                    WalletInteractor walletInteractor){
        return new TransferDetailsPresenter(router, accountInteractor, walletInteractor);
    }
}
