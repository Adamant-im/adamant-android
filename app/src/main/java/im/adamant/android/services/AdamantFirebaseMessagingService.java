package im.adamant.android.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import dagger.android.AndroidInjection;
import im.adamant.android.R;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.helpers.NotificationHelper;
import im.adamant.android.ui.SplashScreen;

import static im.adamant.android.Constants.ADAMANT_DEFAULT_NOTIFICATION_CHANNEL_ID;

public class AdamantFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        LoggerHelper.e("Message", remoteMessage.getMessageId());

        String channelId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = getString(R.string.adamant_default_notification_channel);
            channelId = NotificationHelper.createNotificationChannel(ADAMANT_DEFAULT_NOTIFICATION_CHANNEL_ID, channelName, this);
        }

        String title = getString(R.string.adamant_default_notification_channel);
        String text = getString(R.string.default_notification_message);

        Intent notificationIntent = new Intent(this.getApplicationContext(), SplashScreen.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent = PendingIntent.getActivity(this.getApplicationContext(), 0,
                notificationIntent, 0);

        Notification notification = NotificationHelper.buildMessageNotification(channelId, this, title, text);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notification.contentIntent = intent;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        int id = (int)(remoteMessage.getSentTime() / 1000);
        notificationManager.notify(id, notification);
    }
}
