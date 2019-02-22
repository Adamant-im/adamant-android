package im.adamant.android.dagger.activities;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import im.adamant.android.dagger.fragments.TestBottomLoginModule;
import im.adamant.android.ui.fragments.BottomLoginFragment;

@Module
public abstract class TestLoginScreenModule {
    @ContributesAndroidInjector(modules = {TestBottomLoginModule.class})
    public abstract BottomLoginFragment provideBottomFragment();
}
