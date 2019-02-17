package im.adamant.android.helpers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import im.adamant.android.R;

public class NotificationHelper {
    public static final long[] VIBRATE_PATTERN = new long[] { 1000, 1000, 1000, 1000, 1000 };
    public static final Uri SOUND_URI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    public static Notification buildMessageNotification(String channelId, Context context, String title, String text) {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId);
        notificationBuilder
                .setSmallIcon(getNotificationIcon())
                .setColor(Color.WHITE)
                .setContentTitle(title)
                .setContentText(text)
                .setLights(Color.RED, 3000, 3000)
                .setVibrate(VIBRATE_PATTERN)
                .setSound(SOUND_URI)
                .setAutoCancel(true);

        return notificationBuilder.build();
    }

    public static Notification buildServiceNotification(String channelId, Context context, String title, String text) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId);
        notificationBuilder
                .setSmallIcon(getNotificationIcon())
                .setOngoing(true)
                .setColor(Color.WHITE)
                .setContentTitle(title)
                .setContentText(text)
                .setProgress(100, 0, true);

        return notificationBuilder.build();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public static String createSilentNotificationChannel(String channelId, String channelName, Context context){
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setDescription("Silent channel");
        chan.setSound(null,null);
        chan.enableLights(false);
        chan.setLightColor(Color.BLUE);
        chan.enableVibration(false);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager service = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (service != null) {
            service.createNotificationChannel(chan);
        }

        return channelId;
    }

    public static int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_notification_logo_android5 : R.mipmap.ic_launcher_round;
    }
}
