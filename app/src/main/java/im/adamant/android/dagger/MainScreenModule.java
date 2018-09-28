package im.adamant.android.dagger;

import java.util.Arrays;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import im.adamant.android.R;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.RefreshChatsInteractor;
import im.adamant.android.presenters.MainPresenter;
import im.adamant.android.ui.MainScreen;
import im.adamant.android.ui.adapters.FragmentsAdapter;
import im.adamant.android.ui.fragments.BottomNavigationDrawerFragment;
import im.adamant.android.ui.fragments.ChatsScreen;
import im.adamant.android.ui.fragments.SettingsScreen;
import im.adamant.android.ui.fragments.WalletScreen;
import im.adamant.android.ui.holders.FragmentClassHolder;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

@Module
public abstract class MainScreenModule {
    @FragmentScope
    @ContributesAndroidInjector(modules = {ChatsScreenModule.class})
    public abstract ChatsScreen chatsScreen();

    @FragmentScope
    @ContributesAndroidInjector(modules = {WalletScreenModule.class})
    public abstract WalletScreen walletScreen();

    @FragmentScope
    @ContributesAndroidInjector(modules = {SettingsScreenModule.class})
    public abstract SettingsScreen settingsScreen();

    @FragmentScope
    @ContributesAndroidInjector(modules = {BottomNavigationScreenModule.class})
    public abstract BottomNavigationDrawerFragment drawerFragment();

    @Named("main")
    @ActivityScope
    @Provides
    public static FragmentsAdapter provideFragmentAdapter(MainScreen mainScreen){

        List<FragmentClassHolder> holders = Arrays.asList(
                new FragmentClassHolder(R.string.bottom_menu_title_wallet, WalletScreen.class),
                new FragmentClassHolder(R.string.bottom_menu_title_chats, ChatsScreen.class),
                new FragmentClassHolder(R.string.bottom_menu_title_settings, SettingsScreen.class)
        );

        return new FragmentsAdapter(mainScreen, holders);
    }



}
