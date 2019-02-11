package im.adamant.android.dagger.activities;

import dagger.android.ContributesAndroidInjector;
import im.adamant.android.dagger.fragments.BottomLoginModule;
import im.adamant.android.dagger.fragments.FragmentScope;

import dagger.Module;
import im.adamant.android.ui.fragments.BottomLoginFragment;

@Module
public abstract class LoginScreenModule {

    @FragmentScope
    @ContributesAndroidInjector(modules = {BottomLoginModule.class})
    public abstract BottomLoginFragment provideBottomFragment();
}
