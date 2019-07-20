package im.adamant.android.interactors.push;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public interface PushNotificationServiceFacade {
//    enum Event {
//        SUBSCRIBED,
//        UNSUBSCRIBED
//    }

    int getTitleResource();
    int getShortTitleResource();
    int getDescriptionResource();

    SupportedPushNotificationFacadeType getFacadeType();

    Completable subscribe();
    Completable unsubscribe();

}
