package im.adamant.android.dagger.activities;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import im.adamant.android.dagger.fragments.TestBottomLoginModule;
import im.adamant.android.ui.adapters.WelcomeCardsAdapter;
import im.adamant.android.ui.fragments.BottomLoginFragment;

import static org.mockito.Mockito.mock;

@Module
public abstract class TestLoginScreenModule {
    @ContributesAndroidInjector(modules = {TestBottomLoginModule.class})
    public abstract BottomLoginFragment provideBottomFragment();

    @ActivityScope
    @Provides
    public static WelcomeCardsAdapter provideWelcomeCardAdapter() {
        return  mock(WelcomeCardsAdapter.class);
    }
}
