package im.adamant.android.interactors.push;

import im.adamant.android.R;
import io.reactivex.Completable;
import io.reactivex.Flowable;

public class LocalNotificationServiceFacade implements PushNotificationServiceFacade {
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
        return Completable.complete();
    }

    @Override
    public Completable unsubscribe() {
        return Completable.complete();
    }

}
