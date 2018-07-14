package im.adamant.android.dagger;

import android.content.Context;
import android.support.v4.app.Fragment;

import java.util.Arrays;
import java.util.List;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import im.adamant.android.R;
import im.adamant.android.presenters.MainPresenter;
import im.adamant.android.ui.MainScreen;
import im.adamant.android.ui.adapters.FragmentsAdapter;
import im.adamant.android.ui.fragments.ChatsScreen;
import im.adamant.android.ui.fragments.SettingsScreen;
import im.adamant.android.ui.fragments.WalletScreen;
import im.adamant.android.ui.holders.FragmentClassHolder;
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

    @Named("main")
    @ActivityScope
    @Provides
    public static FragmentsAdapter provideFragmentAdapter(MainScreen mainScreen){

        List<Class> holders = Arrays.asList(
                WalletScreen.class,
                ChatsScreen.class,
                SettingsScreen.class
        );

        return new FragmentsAdapter(mainScreen.getSupportFragmentManager(), mainScreen, holders);
    }

    @ActivityScope
    @Provides
    public static MainPresenter provideLoginPresenter(
            Router router
    ){
        return new MainPresenter(router);
    }
}
