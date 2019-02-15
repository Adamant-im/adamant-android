package im.adamant.android.ui.presenters;

import com.arellomobile.mvp.InjectViewState;

import im.adamant.android.interactors.SwitchPushNotificationServiceInteractor;
import im.adamant.android.ui.mvp_view.PushSubscriptionView;
import io.reactivex.disposables.CompositeDisposable;

@InjectViewState
public class PushSubscriptionPresenter extends BasePresenter<PushSubscriptionView> {
    private SwitchPushNotificationServiceInteractor switchPushNotificationServiceInteractor;

    public PushSubscriptionPresenter(SwitchPushNotificationServiceInteractor switchPushNotificationServiceInteractor, CompositeDisposable subscriptions) {
        super(subscriptions);
        this.switchPushNotificationServiceInteractor = switchPushNotificationServiceInteractor;
    }

    @Override
    public void attachView(PushSubscriptionView view) {
        super.attachView(view);
        getViewState().displayCurrentNotificationFacade(
            switchPushNotificationServiceInteractor.getCurrentFacade()
        );
    }
}
