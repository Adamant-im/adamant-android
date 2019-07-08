package im.adamant.android.dagger.fragments;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Constants;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.LogoutInteractor;
import im.adamant.android.interactors.SecurityInteractor;
import im.adamant.android.interactors.SwitchPushNotificationServiceInteractor;
import im.adamant.android.ui.presenters.SettingsPresenter;
import io.reactivex.Scheduler;
import ru.terrakok.cicerone.Router;

@Module
public class SettingsScreenModule {

    @FragmentScope
    @Provides
    public static SettingsPresenter provideSettingsPresenter(
            Router router,
            AdamantApiWrapper api,
            AccountInteractor accountInteractor,
            LogoutInteractor logoutInteractor,
            SecurityInteractor securityInteractor,
            SwitchPushNotificationServiceInteractor switchPushNotificationServiceInteractor,
            @Named(Constants.UI_SCHEDULER) Scheduler observableScheduler
    ) {
        return new SettingsPresenter(
                router,
                accountInteractor,
                logoutInteractor,
                api,
                securityInteractor,
                switchPushNotificationServiceInteractor,
                observableScheduler
        );
    }

}
