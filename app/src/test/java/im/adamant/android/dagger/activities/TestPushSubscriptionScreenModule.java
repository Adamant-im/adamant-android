package im.adamant.android.dagger.activities;

import java.util.ArrayList;
import java.util.Collection;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.interactors.SwitchPushNotificationServiceInteractor;
import im.adamant.android.interactors.push.PushNotificationServiceFacade;
import im.adamant.android.ui.adapters.PushNotificationServiceAdapter;

import static org.mockito.Mockito.mock;

@Module
public class TestPushSubscriptionScreenModule {
    @ActivityScope
    @Provides
    public static PushNotificationServiceAdapter provideAdapter() {
        return mock(PushNotificationServiceAdapter.class);
    }
}
