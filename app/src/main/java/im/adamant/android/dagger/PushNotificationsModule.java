package im.adamant.android.dagger;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;

import javax.inject.Named;
import javax.inject.Qualifier;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import im.adamant.android.BuildConfig;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.helpers.Settings;
import im.adamant.android.interactors.push.DisabledNotificationServiceFacade;
import im.adamant.android.interactors.push.FCMNotificationServiceFacade;
import im.adamant.android.interactors.push.LocalNotificationServiceFacade;
import im.adamant.android.interactors.push.PushNotificationServiceFacade;
import im.adamant.android.interactors.push.SupportedPushNotificationFacadeType;
import im.adamant.android.interactors.push.SupportedPushNotificationFacadeTypeKey;
import im.adamant.android.receivers.BootCompletedBroadcast;
import im.adamant.android.services.AdamantLocalMessagingService;
import im.adamant.android.ui.messages_support.factories.MessageFactoryProvider;

@Module
public abstract class PushNotificationsModule {
    @Named("AdamantLocalMessagingService")
    @Singleton
    @Provides
    public static LocalNotificationServiceFacade.ApplicationComponentFacade provideServiceComponentFacade(Context context) {
        return enabled -> {
            ComponentName service = new ComponentName(context, AdamantLocalMessagingService.class);
            PackageManager pm = context.getPackageManager();

            if (enabled) {
                pm.setComponentEnabledSetting(service,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);
            } else {
                pm.setComponentEnabledSetting(service,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);
            }
        };
    }

    @Named("BootCompletedBroadcast")
    @Singleton
    @Provides
    public static LocalNotificationServiceFacade.ApplicationComponentFacade provideBootCompletedBroadcastComponentFacade(Context context) {
        return enabled -> {
            ComponentName receiver = new ComponentName(context, BootCompletedBroadcast.class);
            PackageManager pm = context.getPackageManager();

            if (enabled) {
                pm.setComponentEnabledSetting(receiver,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);
            } else {
                pm.setComponentEnabledSetting(receiver,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);
            }
        };
    }


    @Singleton
    @Provides
    public static LocalNotificationServiceFacade.ServicePeriodicallyRunner provideServiceRunner(Context context) {
        return new LocalNotificationServiceFacade.ServicePeriodicallyRunner() {
            private AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            @Override
            public void run(Class<? extends Service> clazz, int id, int period) {
                Intent alarmIntent = new Intent(context, clazz);
                PendingIntent pendingIntent = PendingIntent.getService(context, id, alarmIntent, 0);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis() + period,
                            pendingIntent
                    );
                } else {
                    alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis() + period,
                            pendingIntent
                    );
                }
            }

            @Override
            public void stop(Class<? extends Service> clazz, int id, int period) {
                Intent alarmIntent = new Intent(context, clazz);
                PendingIntent pendingIntent = PendingIntent.getService(context, id, alarmIntent, 0);
                alarmManager.cancel(pendingIntent);
            }
        };
    }

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

    //TODO: Uncomment this when Local Service will be written
//    @IntoMap
//    @SupportedPushNotificationFacadeTypeKey(SupportedPushNotificationFacadeType.LOCAL_SERVICE)
//    @Singleton
//    @Provides
//    public static PushNotificationServiceFacade provideLocalServiceFacade(
//            @Named("AdamantLocalMessagingService") LocalNotificationServiceFacade.ApplicationComponentFacade localServiceComponentFacade,
//            @Named("BootCompletedBroadcast") LocalNotificationServiceFacade.ApplicationComponentFacade bootReceiverComponentFacade,
//            LocalNotificationServiceFacade.ServicePeriodicallyRunner runner
//    ) {
//        return new LocalNotificationServiceFacade(localServiceComponentFacade, bootReceiverComponentFacade, runner);
//    }

    @IntoMap
    @SupportedPushNotificationFacadeTypeKey(SupportedPushNotificationFacadeType.DISABLED)
    @Singleton
    @Provides
    public static PushNotificationServiceFacade provideDisabledFacade() {
        return new DisabledNotificationServiceFacade();
    }
}
