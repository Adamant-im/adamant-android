package im.adamant.android.dagger.activities;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import im.adamant.android.dagger.fragments.TestSendFundsFragmentModule;
import im.adamant.android.ui.adapters.SendCurrencyFragmentAdapter;
import im.adamant.android.ui.fragments.SendFundsFragment;

import static org.mockito.Mockito.mock;

@Module
public abstract class TestSendCurrencyTransferScreenModule {
    @ActivityScope
    @Provides
    public static SendCurrencyFragmentAdapter provideFragmentAdapter() {
        return mock(SendCurrencyFragmentAdapter.class);
    }

    @ContributesAndroidInjector(modules = {TestSendFundsFragmentModule.class})
    public abstract SendFundsFragment createSendCurrencyFragmentInjector();
}
