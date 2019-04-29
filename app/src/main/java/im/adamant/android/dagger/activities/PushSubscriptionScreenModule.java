package im.adamant.android.dagger.activities;

import android.widget.Adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Constants;
import im.adamant.android.Screens;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.SwitchPushNotificationServiceInteractor;
import im.adamant.android.interactors.push.PushNotificationServiceFacade;
import im.adamant.android.interactors.push.SupportedPushNotificationFacadeType;
import im.adamant.android.ui.adapters.PushNotificationServiceAdapter;
import im.adamant.android.ui.presenters.PushSubscriptionPresenter;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

@Module
public class PushSubscriptionScreenModule {
    @ActivityScope
    @Provides
    public static PushNotificationServiceAdapter provideAdapter(SwitchPushNotificationServiceInteractor switchPushNotificationServiceInteractor) {
        Collection<PushNotificationServiceFacade> values = switchPushNotificationServiceInteractor.getFacades().values();
        ArrayList<PushNotificationServiceFacade> list = new ArrayList<>(values);
        return new PushNotificationServiceAdapter(list);
    }

    @ActivityScope
    @Provides
    public static PushSubscriptionPresenter providePushSubscriptionPresenter(
            SwitchPushNotificationServiceInteractor switchPushNotificationServiceInteractor,
            AccountInteractor accountInteractor,
            Router router,
            @Named(Constants.UI_SCHEDULER) Scheduler observableScheduler
    ){
        return new PushSubscriptionPresenter(
                router,
                accountInteractor,
                switchPushNotificationServiceInteractor,
                observableScheduler
        );
    }
}
