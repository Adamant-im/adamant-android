package im.adamant.android.interactors.push;

import im.adamant.android.R;
import io.reactivex.Completable;


public class DisabledNotificationServiceFacade implements PushNotificationServiceFacade {
    @Override
    public int getTitleResource() {
        return R.string.disabled_notification_service_full;
    }

    @Override
    public int getShortTitleResource() {
        return R.string.disabled_notification_service_short;
    }

    @Override
    public int getDescriptionResource() {
        return R.string.disabled_notification_service_description;
    }

    @Override
    public SupportedPushNotificationFacadeType getFacadeType() {
        return SupportedPushNotificationFacadeType.DISABLED;
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
