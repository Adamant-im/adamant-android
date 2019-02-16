package im.adamant.android.interactors;

import org.reactivestreams.Publisher;

import java.util.Map;

import im.adamant.android.core.exceptions.NotSupportedPushNotificationFacade;
import im.adamant.android.helpers.Settings;
import im.adamant.android.interactors.push.PushNotificationServiceFacade;
import im.adamant.android.interactors.push.SupportedPushNotificationFacadeType;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

public class SwitchPushNotificationServiceInteractor {
    private Settings settings;
    private Map<SupportedPushNotificationFacadeType, PushNotificationServiceFacade> facades;

    public SwitchPushNotificationServiceInteractor(
            Settings settings,
            Map<SupportedPushNotificationFacadeType, PushNotificationServiceFacade> facades
    ) {
        this.settings = settings;
        this.facades = facades;
    }

    public Completable changeNotificationFacade(SupportedPushNotificationFacadeType newFacadeType) {
        SupportedPushNotificationFacadeType pushNotificationFacadeType = settings.getPushNotificationFacadeType();
        PushNotificationServiceFacade currentFacade = facades.get(pushNotificationFacadeType);
        PushNotificationServiceFacade newFacade = facades.get(newFacadeType);

        if ((currentFacade != null) && (newFacade != null)) {
            return currentFacade
                    .unsubscribe()
                    .andThen(newFacade.subscribe())
                    .doOnComplete(() -> settings.setPushNotificationFacadeType(newFacadeType));
        } else {
            return Completable.error(new NotSupportedPushNotificationFacade("Not Supported facade: " + pushNotificationFacadeType));
        }
    }

    public Completable resetNotificationFacade(boolean value) {
        if (value) {
            return changeNotificationFacade(SupportedPushNotificationFacadeType.DISABLED);
        } else {
            return Completable.complete();
        }
    }

    public PushNotificationServiceFacade getCurrentFacade() {
        SupportedPushNotificationFacadeType pushNotificationFacadeType = settings.getPushNotificationFacadeType();
        return facades.get(pushNotificationFacadeType);
    }

    public Map<SupportedPushNotificationFacadeType, PushNotificationServiceFacade> getFacades() {
        return facades;
    }
}
