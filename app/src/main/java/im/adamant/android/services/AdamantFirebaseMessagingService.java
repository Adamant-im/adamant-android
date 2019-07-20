package im.adamant.android.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.os.Build;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import im.adamant.android.BuildConfig;
import im.adamant.android.R;
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.core.responses.Authorization;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.helpers.NotificationHelper;
import im.adamant.android.helpers.Settings;
import im.adamant.android.interactors.SecurityInteractor;
import im.adamant.android.interactors.push.FCMNotificationServiceFacade;
import im.adamant.android.interactors.push.PushNotificationServiceFacade;
import im.adamant.android.interactors.push.SupportedPushNotificationFacadeType;
import im.adamant.android.ui.BaseActivity;
import im.adamant.android.ui.SplashScreen;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import static im.adamant.android.Constants.ADAMANT_DEFAULT_NOTIFICATION_CHANNEL_ID;

public class AdamantFirebaseMessagingService extends FirebaseMessagingService {
    private static final int NOTIFICATION_ID = 123445;

    @Inject
    SecurityInteractor securityInteractor;

    @Inject
    Map<SupportedPushNotificationFacadeType, PushNotificationServiceFacade> facades;

    @Inject
    Settings settings;

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        LoggerHelper.d("FCM", "RECEIVED PUSH-NOTIFICATION: " + remoteMessage.getMessageId());

        if (settings.getPushNotificationFacadeType() != SupportedPushNotificationFacadeType.FCM) {
            return;
        }

        if (!BaseActivity.isActivityInForeground()){
            showMessageNotification();
        }
    }

    private void showMessageNotification() {
        String title = getString(R.string.adamant_default_notification_channel);
        String text = getString(R.string.default_notification_message);

        showNotification(title, text);
    }

    private void showNotification(String title, String message) {
        String channelId = buildChannel();

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
}
