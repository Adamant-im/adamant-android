package im.adamant.android.dagger.activities;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.ui.adapters.CurrencyTransfersAdapter;
import im.adamant.android.ui.presenters.AllTransactionsPresenter;

import static org.mockito.Mockito.mock;

@Module
public class TestAllTransactionsScreenModule {
    @ActivityScope
    @Provides
    public CurrencyTransfersAdapter provideAdapter(){
        return mock(CurrencyTransfersAdapter.class);
    }

    @ActivityScope
    @Provides
    public static AllTransactionsPresenter provideAllTransactionsPresenter() {
        return mock(AllTransactionsPresenter.class);
    }
}
