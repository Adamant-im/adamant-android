package im.adamant.android.interactors.push;

import android.app.Service;
import android.content.ComponentName;

import im.adamant.android.BuildConfig;
import im.adamant.android.R;
import im.adamant.android.services.AdamantLocalMessagingService;
import io.reactivex.Completable;
import io.reactivex.Flowable;

public class LocalNotificationServiceFacade implements PushNotificationServiceFacade {
    public static final int ADAMANT_LOCAL_MESSAGING_SERVICE = 93632;

    private ApplicationComponentFacade localServiceComponent;
    private ApplicationComponentFacade bootReceiverComponent;
    private ServicePeriodicallyRunner runner;

    public interface ApplicationComponentFacade {
        void setEnabled(boolean enabled);
    }

    public interface ServicePeriodicallyRunner {
        void run(Class<? extends Service> clazz, int id, int period);
        void stop(Class<? extends Service> clazz, int id, int period);
    }

    public LocalNotificationServiceFacade(
            ApplicationComponentFacade localServiceComponent,
            ApplicationComponentFacade bootReceiverComponent,
            ServicePeriodicallyRunner runner
    ) {
        this.localServiceComponent = localServiceComponent;
        this.bootReceiverComponent = bootReceiverComponent;
        this.runner = runner;
    }

    @Override
    public int getTitleResource() {
        return R.string.local_notification_service_full;
    }

    @Override
    public int getShortTitleResource() {
        return R.string.local_notification_service_short;
    }

    @Override
    public int getDescriptionResource() {
        return R.string.local_notification_service_description;
    }

    @Override
    public SupportedPushNotificationFacadeType getFacadeType() {
        return SupportedPushNotificationFacadeType.LOCAL_SERVICE;
    }

    @Override
    public Completable subscribe() {
        return Completable.fromAction(() -> {
            localServiceComponent.setEnabled(true);
            bootReceiverComponent.setEnabled(true);
            runner.run(
                    AdamantLocalMessagingService.class,
                    ADAMANT_LOCAL_MESSAGING_SERVICE,
                    BuildConfig.LOCAL_NOTIFICATION_SERVICE_CALL_MINUTES_DELAY * 60 * 1000
            );
        });
    }

    @Override
    public Completable unsubscribe() {
        return Completable.fromAction(() -> {
            runner.stop(
                    AdamantLocalMessagingService.class,
                    ADAMANT_LOCAL_MESSAGING_SERVICE,
                    BuildConfig.LOCAL_NOTIFICATION_SERVICE_CALL_MINUTES_DELAY * 60 * 1000
            );
            localServiceComponent.setEnabled(false);
            bootReceiverComponent.setEnabled(false);
        });
    }

}
