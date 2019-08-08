package im.adamant.android.dagger.activities;

import java.util.Arrays;
import java.util.List;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import im.adamant.android.R;
import im.adamant.android.dagger.fragments.ChatsScreenModule;
import im.adamant.android.dagger.fragments.CreateChatScreenModule;
import im.adamant.android.dagger.fragments.FragmentScope;
import im.adamant.android.dagger.fragments.SettingsScreenModule;
import im.adamant.android.dagger.fragments.WalletScreenModule;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.SwitchPushNotificationServiceInteractor;
import im.adamant.android.interactors.chats.ChatInteractor;
import im.adamant.android.ui.MainScreen;
import im.adamant.android.ui.adapters.FragmentsAdapter;
import im.adamant.android.ui.fragments.ChatsScreen;
import im.adamant.android.ui.fragments.CreateChatFragment;
import im.adamant.android.ui.fragments.SettingsScreen;
import im.adamant.android.ui.fragments.WalletScreen;
import im.adamant.android.ui.holders.FragmentClassHolder;
import im.adamant.android.ui.presenters.MainPresenter;
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
    @ContributesAndroidInjector(modules = {CreateChatScreenModule.class})
    public abstract CreateChatFragment createChatScreenInjector();

    @Named("main")
    @ActivityScope
    @Provides
    public static FragmentsAdapter provideFragmentAdapter(MainScreen mainScreen) {

        List<FragmentClassHolder> holders = Arrays.asList(
                new FragmentClassHolder(R.string.bottom_menu_title_wallet, WalletScreen.class),
                new FragmentClassHolder(R.string.bottom_menu_title_chats, ChatsScreen.class),
                new FragmentClassHolder(R.string.bottom_menu_title_settings, SettingsScreen.class)
        );

        return new FragmentsAdapter(mainScreen, holders);
    }

    @ActivityScope
    @Provides
    public static MainPresenter provideMainPresenter(
            Router router,
            AccountInteractor accountInteractor,
            SwitchPushNotificationServiceInteractor pushNotificationServiceInteractor,
            ChatInteractor chatInteractor
    ) {
        return new MainPresenter(router, pushNotificationServiceInteractor, accountInteractor,
                chatInteractor);
    }

}
