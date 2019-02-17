package im.adamant.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import im.adamant.android.interactors.SwitchPushNotificationServiceInteractor;
import im.adamant.android.interactors.push.PushNotificationServiceFacade;
import im.adamant.android.interactors.push.SupportedPushNotificationFacadeType;
import im.adamant.android.services.AdamantLocalMessagingService;

public class BootCompletedBroadcast extends BroadcastReceiver {

    @Inject
    SwitchPushNotificationServiceInteractor switchPushNotificationServiceInteractor;

    @Override
    public void onReceive(Context context, Intent intent) {
        AndroidInjection.inject(this, context);

        if (intent == null || intent.getAction() == null) { return; }

        switch (intent.getAction()) {
            case Intent.ACTION_LOCKED_BOOT_COMPLETED:
            case Intent.ACTION_BOOT_COMPLETED:
                PushNotificationServiceFacade currentFacade = switchPushNotificationServiceInteractor.getCurrentFacade();
                boolean isCurrentLocalService = currentFacade != null && SupportedPushNotificationFacadeType.LOCAL_SERVICE.equals(currentFacade.getFacadeType());
                if (isCurrentLocalService) {
                    Intent i = new Intent(context, AdamantLocalMessagingService.class);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(i);
                    } else {
                        context.startService(i);
                    }
                }
                break;
        }
    }
}
