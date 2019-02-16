package im.adamant.android.dagger.activities;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Screens;
import im.adamant.android.interactors.SwitchPushNotificationServiceInteractor;
import im.adamant.android.ui.presenters.PushSubscriptionPresenter;
import io.reactivex.disposables.CompositeDisposable;


//TODO: Create Presenters module
@Module
public class PushSubscriptionScreenModule {
    @ActivityScope
    @Provides
    public static PushSubscriptionPresenter providePushSubscriptionPresenter(
            SwitchPushNotificationServiceInteractor switchPushNotificationServiceInteractor,
            @Named(Screens.PUSH_SUBSCRIPTION_SCREEN) CompositeDisposable subscriptions
    ){
        return new PushSubscriptionPresenter(
                switchPushNotificationServiceInteractor,
                subscriptions
        );
    }

    @ActivityScope
    @Provides
    @Named(value = Screens.PUSH_SUBSCRIPTION_SCREEN)
    public static CompositeDisposable provideComposite() {
        return new CompositeDisposable();
    }
}
