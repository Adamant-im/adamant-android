package im.adamant.android.dagger.activities;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.WalletInteractor;
import im.adamant.android.ui.adapters.CurrencyTransfersAdapter;
import im.adamant.android.ui.presenters.AllTransactionsPresenter;
import ru.terrakok.cicerone.Router;

@Module
public class AllTransactionsScreenModule {
    @ActivityScope
    @Provides
    public CurrencyTransfersAdapter provideAdapter(){
        return new CurrencyTransfersAdapter();
    }

    @ActivityScope
    @Provides
    public static AllTransactionsPresenter provideAllTransactionsPresenter(
            Router router,
            AccountInteractor accountInteractor,
            WalletInteractor walletInteractor
    ) {
        return new AllTransactionsPresenter(
                router,
                accountInteractor,
                walletInteractor
        );
    }
}
