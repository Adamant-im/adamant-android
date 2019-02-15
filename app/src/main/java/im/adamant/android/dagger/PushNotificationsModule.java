package im.adamant.android.dagger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import im.adamant.android.helpers.Settings;
import im.adamant.android.interactors.push.DisabledNotificationServiceFacade;
import im.adamant.android.interactors.push.FCMNotificationServiceFacade;
import im.adamant.android.interactors.push.LocalNotificationServiceFacade;
import im.adamant.android.interactors.push.PushNotificationServiceFacade;
import im.adamant.android.interactors.push.SupportedPushNotificationFacadeType;
import im.adamant.android.interactors.push.SupportedPushNotificationFacadeTypeKey;
import im.adamant.android.ui.messages_support.factories.MessageFactoryProvider;

@Module
public class PushNotificationsModule {
    @IntoMap
    @SupportedPushNotificationFacadeTypeKey(SupportedPushNotificationFacadeType.FCM)
    @Singleton
    @Provides
    public static PushNotificationServiceFacade provideFcmFacade(
            Settings settings,
            MessageFactoryProvider messageFactoryProvider
    ) {
        return new FCMNotificationServiceFacade(settings, messageFactoryProvider);
    }

    @IntoMap
    @SupportedPushNotificationFacadeTypeKey(SupportedPushNotificationFacadeType.LOCAL_SERVICE)
    @Singleton
    @Provides
    public static PushNotificationServiceFacade provideLocalServiceFacade() {
        return new LocalNotificationServiceFacade();
    }

    @IntoMap
    @SupportedPushNotificationFacadeTypeKey(SupportedPushNotificationFacadeType.DISABLED)
    @Singleton
    @Provides
    public static PushNotificationServiceFacade provideDisabledFacade() {
        return new DisabledNotificationServiceFacade();
    }
}
