package im.adamant.android.dagger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import im.adamant.android.interactors.push.DisabledNotificationServiceFacade;
import im.adamant.android.interactors.push.FCMNotificationServiceFacade;
import im.adamant.android.interactors.push.LocalNotificationServiceFacade;
import im.adamant.android.interactors.push.PushNotificationServiceFacade;
import im.adamant.android.interactors.push.SupportedPushNotificationFacadeType;
import im.adamant.android.interactors.push.SupportedPushNotificationFacadeTypeKey;

import static org.mockito.Mockito.mock;

@Module
public class TestPushNotificationModule {
    @IntoMap
    @SupportedPushNotificationFacadeTypeKey(SupportedPushNotificationFacadeType.FCM)
    @Singleton
    @Provides
    public static PushNotificationServiceFacade provideFcmFacade() {
        return mock(FCMNotificationServiceFacade.class);
    }

    @IntoMap
    @SupportedPushNotificationFacadeTypeKey(SupportedPushNotificationFacadeType.LOCAL_SERVICE)
    @Singleton
    @Provides
    public static PushNotificationServiceFacade provideLocalServiceFacade() {
        return mock(LocalNotificationServiceFacade.class);
    }

    @IntoMap
    @SupportedPushNotificationFacadeTypeKey(SupportedPushNotificationFacadeType.DISABLED)
    @Singleton
    @Provides
    public static PushNotificationServiceFacade provideDisabledFacade() {
        return mock(DisabledNotificationServiceFacade.class);
    }
}
