package im.adamant.android.dagger;

import javax.inject.Named;
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
    @Named("AdamantLocalMessagingService")
    @Singleton
    @Provides
    public static LocalNotificationServiceFacade.ApplicationComponentFacade provideServiceComponentFacade() {
        return mock(LocalNotificationServiceFacade.ApplicationComponentFacade.class);
    }

    @Named("BootCompletedBroadcast")
    @Singleton
    @Provides
    public static LocalNotificationServiceFacade.ApplicationComponentFacade provideBootCompletedBroadcastComponentFacade() {
        return mock(LocalNotificationServiceFacade.ApplicationComponentFacade.class);
    }
    
    @Singleton
    @Provides
    public static LocalNotificationServiceFacade.ServicePeriodicallyRunner provideServiceRunner() {
        return mock(LocalNotificationServiceFacade.ServicePeriodicallyRunner.class);
    }

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
