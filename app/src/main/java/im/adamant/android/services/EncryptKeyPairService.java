package im.adamant.android.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import java.lang.ref.WeakReference;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.android.AndroidInjection;
import im.adamant.android.R;
import im.adamant.android.dagger.EncryptKeyPairServiceModule;
import im.adamant.android.interactors.SettingsInteractor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class EncryptKeyPairService extends Service {
    public static final String VALUE_KEY = "value";
    private static final int NOTIFICATION_ID = 34242;

    @Inject
    SettingsInteractor settingsInteractor;

    @Named(EncryptKeyPairServiceModule.NAME)
    @Inject
    CompositeDisposable subscriptions;

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
        startForeground(NOTIFICATION_ID, buildNotification());
        if (intent.getExtras() != null){
            boolean value = intent.getExtras().getBoolean(VALUE_KEY, false);
            saveKeyPair(value);
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        subscriptions.dispose();
        super.onDestroy();
    }

    private void saveKeyPair(boolean value) {
        WeakReference<Service> thisReference = new WeakReference<>(this);
        Disposable subscribe = settingsInteractor
                .saveKeypair(value)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    () -> stopForeground(thisReference),
                    (error) -> stopForeground(thisReference)
                );
        subscriptions.add(subscribe);
    }

    private static void stopForeground(WeakReference<Service> serviceWeakReference) {
        Service service = serviceWeakReference.get();
        if (service != null) {
            service.stopForeground(true);
            service.stopSelf();
        }
    }

    private Notification buildNotification() {
        String channelId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel();
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setOngoing(true)
                .setColor(Color.WHITE)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.encrypt_keypair_notification_message))
                .setProgress(100, 0, true);

        return notificationBuilder.build();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(){
        String channelId = "adamant_keypair_encryption";
        String channelName = getString(R.string.encrypt_keypair_notification_channel);
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (service != null) {
            service.createNotificationChannel(chan);
        }

        return channelId;
    }
}
