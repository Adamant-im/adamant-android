package im.adamant.android.services;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.android.AndroidInjection;
import im.adamant.android.R;
import im.adamant.android.dagger.SaveContactsServiceModule;
import im.adamant.android.helpers.NotificationHelper;
import im.adamant.android.interactors.SaveContactsInteractor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static im.adamant.android.Constants.ADAMANT_SYSTEM_NOTIFICATION_CHANNEL_ID;

public class SaveContactsService extends Service {
    private static final int NOTIFICATION_ID = 782372;

    @Inject
    SaveContactsInteractor saveContactsInteractor;

    @Named(SaveContactsServiceModule.NAME)
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
        String channelId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = getString(R.string.adamant_system_notification_channel);
            channelId = NotificationHelper.createNotificationChannel(ADAMANT_SYSTEM_NOTIFICATION_CHANNEL_ID, channelName, this);
        }

        String title = getString(R.string.app_name);
        String text = getString(R.string.save_contacts_notification_message);

        startForeground(NOTIFICATION_ID, NotificationHelper.buildServiceNotification(channelId, this, title, text));
        saveContacts();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        subscriptions.dispose();
        super.onDestroy();
    }

    private void saveContacts() {
        WeakReference<Service> serviceWeakReference = new WeakReference<>(this);
        Disposable subscribe = saveContactsInteractor
                .execute()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    () -> stopForeground(serviceWeakReference),
                    (error) -> stopForeground(serviceWeakReference)
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

}
