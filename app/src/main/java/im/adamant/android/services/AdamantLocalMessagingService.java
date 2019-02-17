package im.adamant.android.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import dagger.android.AndroidInjection;
import im.adamant.android.BuildConfig;
import im.adamant.android.R;
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.helpers.NotificationHelper;
import im.adamant.android.interactors.AuthorizeInteractor;
import im.adamant.android.interactors.HasNewMessagesInteractor;
import im.adamant.android.interactors.SwitchPushNotificationServiceInteractor;
import im.adamant.android.interactors.push.LocalNotificationServiceFacade;
import im.adamant.android.interactors.push.PushNotificationServiceFacade;
import im.adamant.android.interactors.push.SupportedPushNotificationFacadeType;
import im.adamant.android.ui.SplashScreen;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;

import static im.adamant.android.Constants.ADAMANT_DEFAULT_NOTIFICATION_CHANNEL_ID;
import static im.adamant.android.Constants.ADAMANT_SYSTEM_NOTIFICATION_CHANNEL_ID;

public class AdamantLocalMessagingService extends Service {
    private static final int NOTIFICATION_ID = 123445;

    @Inject
    SwitchPushNotificationServiceInteractor switchPushNotificationServiceInteractor;

    @Inject
    HasNewMessagesInteractor hasNewMessagesInteractor;

    @Inject
    AuthorizeInteractor authorizeInteractor;

    @Inject
    LocalNotificationServiceFacade.ServicePeriodicallyRunner runner;

    Disposable listenerSubscription;

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LoggerHelper.d("ALSNotify", "START NOTIFICATION SCANNING");
        PushNotificationServiceFacade currentFacade = switchPushNotificationServiceInteractor.getCurrentFacade();
        boolean isCurrentLocalService = currentFacade != null && SupportedPushNotificationFacadeType.LOCAL_SERVICE.equals(currentFacade.getFacadeType());
        if (isCurrentLocalService) {

            String channelId = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelName = getString(R.string.adamant_system_notification_channel);
                channelId = NotificationHelper.createSilentNotificationChannel(ADAMANT_SYSTEM_NOTIFICATION_CHANNEL_ID, channelName, this);
            }

            String title = getString(R.string.app_name);
            String text = getString(R.string.new_message_scanning_notification_message);

            startForeground(NOTIFICATION_ID, NotificationHelper.buildServiceNotification(channelId, this, title, text));

            startScan();

            runner.run(
                    getClass(),
                    LocalNotificationServiceFacade.ADAMANT_LOCAL_MESSAGING_SERVICE,
                    BuildConfig.LOCAL_NOTIFICATION_SERVICE_CALL_MINUTES_DELAY * 60 * 1000
            );

            return START_NOT_STICKY;
        }

        stopSelf();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (listenerSubscription != null) {
            listenerSubscription.dispose();
        }
        super.onDestroy();
    }

    private void startScan() {
        if (listenerSubscription != null) {
            listenerSubscription.dispose();
        }

        WeakReference<AdamantLocalMessagingService> serviceWeakReference = new WeakReference<>(this);
        listenerSubscription = authorizeInteractor
                .restoreAuthorization()
                .flatMap(authorization -> {
                    if (authorization.isSuccess()) {
                        return hasNewMessagesInteractor.execute();
                    } else {
                        return Flowable.error(new NotAuthorizedException(authorization.getError()));
                    }
                })
                .filter((event) -> event == HasNewMessagesInteractor.Event.HAS_NEW_MESSAGES)
                .subscribe(
                        (event) -> {
                            LoggerHelper.d("EVENT", event.name());
                            AdamantLocalMessagingService service = serviceWeakReference.get();
                            if (service != null){
                                service.showMessageNotification();
                            }
                            stopForeground(serviceWeakReference);
                        },
                        (error) -> {
                            LoggerHelper.e("AdmLocalService", error.getMessage());
                            stopForeground(serviceWeakReference);
                        },
                        () -> stopForeground(serviceWeakReference)
                );
    }

    private void showMessageNotification() {
        String title = getString(R.string.adamant_default_notification_channel);
        String text = getString(R.string.default_notification_message);

        showNotification(title, text);
    }

    private void showNotification(String title, String message) {
        String channelId = buildChannel();

        LoggerHelper.d("ALSNotify", "notify: " + title + " : " + message);

        Intent notificationIntent = new Intent(this.getApplicationContext(), SplashScreen.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent = PendingIntent.getActivity(this.getApplicationContext(), 0,
                notificationIntent, 0);

        Notification notification = NotificationHelper.buildMessageNotification(channelId, this, title, message);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notification.contentIntent = intent;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private String buildChannel() {
        String channelId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = getString(R.string.adamant_default_notification_channel);

            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            NotificationChannel chan = new NotificationChannel(ADAMANT_DEFAULT_NOTIFICATION_CHANNEL_ID,
                    channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.RED);
            chan.enableLights(true);
            chan.enableVibration(true);
            chan.setVibrationPattern(NotificationHelper.VIBRATE_PATTERN);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            chan.setSound(NotificationHelper.SOUND_URI, attributes);

            NotificationManager service = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            if (service != null) {
                service.createNotificationChannel(chan);
                channelId = ADAMANT_DEFAULT_NOTIFICATION_CHANNEL_ID;
            }
        }

        return channelId;
    }

    private static void stopForeground(WeakReference<AdamantLocalMessagingService> serviceWeakReference) {
        Service service = serviceWeakReference.get();
        if (service != null) {
            service.stopForeground(true);
            service.stopSelf();
        }
    }
}
