package im.adamant.android.dagger.activities;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import im.adamant.android.dagger.fragments.TestBottomNavigationScreenModule;
import im.adamant.android.dagger.fragments.TestChatsScreenModule;
import im.adamant.android.dagger.fragments.TestCreateChatScreenModule;
import im.adamant.android.dagger.fragments.TestSettingsScreenModule;
import im.adamant.android.dagger.fragments.TestWalletScreenModule;
import im.adamant.android.ui.MainScreen;
import im.adamant.android.ui.adapters.FragmentsAdapter;
import im.adamant.android.ui.fragments.BottomNavigationDrawerFragment;
import im.adamant.android.ui.fragments.ChatsScreen;
import im.adamant.android.ui.fragments.CreateChatFragment;
import im.adamant.android.ui.fragments.SettingsScreen;
import im.adamant.android.ui.fragments.WalletScreen;

import static org.mockito.Mockito.mock;

@Module
public abstract class TestMainScreenModule {
    @ContributesAndroidInjector(modules = {TestChatsScreenModule.class})
    public abstract ChatsScreen chatsScreen();

    @ContributesAndroidInjector(modules = {TestWalletScreenModule.class})
    public abstract WalletScreen walletScreen();

    @ContributesAndroidInjector(modules = {TestSettingsScreenModule.class})
    public abstract SettingsScreen settingsScreen();

    @ContributesAndroidInjector(modules = {TestBottomNavigationScreenModule.class})
    public abstract BottomNavigationDrawerFragment drawerFragment();

    @ContributesAndroidInjector(modules = {TestCreateChatScreenModule.class})
    public abstract CreateChatFragment createChatScreenInjector();

    @ActivityScope
    @Named("main")
    @Provides
    public static FragmentsAdapter provideFragmentAdapter(MainScreen mainScreen){
        return mock(FragmentsAdapter.class);
    }



}
