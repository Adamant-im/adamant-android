package im.adamant.android.services;

import android.app.Notification;
import android.os.Build;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import im.adamant.android.R;
import im.adamant.android.helpers.NotificationHelper;
import im.adamant.android.interactors.SendMessageInteractor;
import im.adamant.android.ui.messages_support.SupportedMessageTypes;
import im.adamant.android.ui.messages_support.entities.AdamantPushSubscriptionMessage;
import im.adamant.android.ui.messages_support.factories.AdamantPushSubscriptionMessageFactory;
import im.adamant.android.ui.messages_support.factories.MessageFactory;
import im.adamant.android.ui.messages_support.factories.MessageFactoryProvider;

import static im.adamant.android.Constants.ADAMANT_DEFAULT_NOTIFICATION_CHANNEL_ID;
import static im.adamant.android.Constants.ADAMANT_SYSTEM_NOTIFICATION_CHANNEL_ID;

public class AdamantFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e("Message", remoteMessage.getMessageId());

        String channelId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = getString(R.string.adamant_default_notification_channel);
            channelId = NotificationHelper.createNotificationChannel(ADAMANT_DEFAULT_NOTIFICATION_CHANNEL_ID, channelName, this);
        }

        String title = getString(R.string.adamant_default_notification_channel);
        String text = getString(R.string.default_notification_message);

        Notification notification = NotificationHelper.buildMessageNotification(channelId, this, title, text);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        int id = (int)(remoteMessage.getSentTime() / 1000);
        notificationManager.notify(id, notification);
    }
}
