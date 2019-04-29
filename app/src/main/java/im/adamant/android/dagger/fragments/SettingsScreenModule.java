package im.adamant.android.dagger.fragments;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Screens;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.SecurityInteractor;
import im.adamant.android.interactors.SwitchPushNotificationServiceInteractor;
import im.adamant.android.ui.presenters.SettingsPresenter;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

@Module
public class SettingsScreenModule {

    @FragmentScope
    @Provides
    public static SettingsPresenter provideSettingsPresenter(
            Router router,
            AdamantApiWrapper api,
            AccountInteractor accountInteractor,
            SecurityInteractor securityInteractor,
            SwitchPushNotificationServiceInteractor switchPushNotificationServiceInteractor
    ) {
        return new SettingsPresenter(
                router,
                accountInteractor,
                api,
                securityInteractor,
                switchPushNotificationServiceInteractor
        );
    }

}
