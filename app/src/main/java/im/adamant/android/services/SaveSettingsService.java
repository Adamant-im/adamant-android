package im.adamant.android.services;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.iid.FirebaseInstanceId;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.android.AndroidInjection;
import im.adamant.android.dagger.SaveSettingsServiceModule;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.SaveKeypairInteractor;
import im.adamant.android.interactors.SubscribeToPushInteractor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import sm.euzee.github.com.servicemanager.CompatService;

public class SaveSettingsService extends CompatService {
    public static final String IS_SAVE_KEYPAIR = "is_save_keypair";
    public static final String IS_RECEIVE_NOTIFICATIONS = "is_receive_notifications";
    public static final String NOTIFICATION_SERVICE_ADDRESS = "notification_service_address";

    @Inject
    SaveKeypairInteractor settingsInteractor;

    @Inject
    SubscribeToPushInteractor subscribeToPushInteractor;

    @Named(SaveSettingsServiceModule.NAME)
    @Inject
    CompositeDisposable subscriptions;


    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        subscriptions.dispose();
        super.onDestroy();
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (intent.getExtras() != null){
            LoggerHelper.d("Settings", "Saving settings");
            boolean isSaveKeypair = intent.getExtras().getBoolean(IS_SAVE_KEYPAIR, false);
            saveKeyPair(isSaveKeypair);

            boolean isSubscribeToNotifications = intent.getExtras().getBoolean(IS_RECEIVE_NOTIFICATIONS, false);
            String addressOfNotificationService = intent.getExtras().getString(NOTIFICATION_SERVICE_ADDRESS, subscribeToPushInteractor.getPushServiceAddress());
            savePushSettings(isSubscribeToNotifications, addressOfNotificationService);
        }
    }

    private void savePushSettings(boolean enable, String address) {
        subscribeToPushInteractor.savePushConfig(enable, address);
        CompositeDisposable localSubscriptions = subscriptions;

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            String deviceToken = instanceIdResult.getToken();
            Disposable subscribe = subscribeToPushInteractor
                    .savePushToken(deviceToken)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError((error) -> LoggerHelper.e("savePushToken", error.getMessage(), error))
                    .subscribe();
            localSubscriptions.add(subscribe);
        });
    }

    private void saveKeyPair(boolean value) {
        Disposable subscribe = settingsInteractor
                .saveKeypair(value)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError((error) -> LoggerHelper.e("saveKeyPair", error.getMessage(), error))
                .subscribe();
        subscriptions.add(subscribe);
    }
}
