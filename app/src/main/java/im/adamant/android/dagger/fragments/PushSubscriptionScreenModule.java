package im.adamant.android.dagger.fragments;

import java.util.ArrayList;
import java.util.Collection;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.interactors.SwitchPushNotificationServiceInteractor;
import im.adamant.android.interactors.push.PushNotificationServiceFacade;
import im.adamant.android.ui.adapters.PushNotificationServiceAdapter;

@Module
public class PushSubscriptionScreenModule {
    @FragmentScope
    @Provides
    public static PushNotificationServiceAdapter provideAdapter(SwitchPushNotificationServiceInteractor switchPushNotificationServiceInteractor) {
        Collection<PushNotificationServiceFacade> values = switchPushNotificationServiceInteractor.getFacades().values();
        ArrayList<PushNotificationServiceFacade> list = new ArrayList<>(values);
        return new PushNotificationServiceAdapter(list);
    }

}
