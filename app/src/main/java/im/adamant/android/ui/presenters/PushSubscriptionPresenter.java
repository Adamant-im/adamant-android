package im.adamant.android.ui.presenters;

import com.arellomobile.mvp.InjectViewState;

import java.util.ArrayList;

import im.adamant.android.R;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.SwitchPushNotificationServiceInteractor;
import im.adamant.android.interactors.push.PushNotificationServiceFacade;
import im.adamant.android.ui.mvp_view.PushSubscriptionView;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

@InjectViewState
public class PushSubscriptionPresenter extends BasePresenter<PushSubscriptionView> {
    private SwitchPushNotificationServiceInteractor switchPushNotificationServiceInteractor;
    private Scheduler observeScheduler;

    public PushSubscriptionPresenter(
            SwitchPushNotificationServiceInteractor switchPushNotificationServiceInteractor,
            Scheduler observeScheduler
    ) {
        this.observeScheduler = observeScheduler;
        this.switchPushNotificationServiceInteractor = switchPushNotificationServiceInteractor;
    }

    @Override
    public void attachView(PushSubscriptionView view) {
        super.attachView(view);
        getViewState().displayCurrentNotificationFacade(
            switchPushNotificationServiceInteractor.getCurrentFacade()
        );
    }

    public void showSelectServiceDialog() {
        getViewState().showSelectServiceDialog(
                new ArrayList<>(switchPushNotificationServiceInteractor.getFacades().values()),
                switchPushNotificationServiceInteractor.getCurrentFacade()
        );
    }

    public void onClickSetNewPushService(PushNotificationServiceFacade facade) {
        if (facade != null) {
            getViewState().setEnablePushServiceTypeOption(false);
            getViewState().startProgress();
            Disposable subscribe = switchPushNotificationServiceInteractor
                    .changeNotificationFacade(facade.getFacadeType())
                    .doOnError((error) -> LoggerHelper.e("SWITCH NOTIFICATION SERVICE", error.getMessage(), error))
                    .observeOn(observeScheduler)
                    .subscribe(
                            () -> {
                                getViewState().setEnablePushServiceTypeOption(true);
                                getViewState().stopProgress();
                                getViewState().showMessage(R.string.fragment_settings_success_saved);
                                getViewState().displayCurrentNotificationFacade(
                                        switchPushNotificationServiceInteractor.getCurrentFacade()
                                );
                            },
                            (error) -> {
                                getViewState().setEnablePushServiceTypeOption(true);
                                getViewState().stopProgress();
                                getViewState().showMessage(error.getMessage());
                                getViewState().displayCurrentNotificationFacade(
                                        switchPushNotificationServiceInteractor.getCurrentFacade()
                                );
                            }
                    );
            subscriptions.add(subscribe);
        }
    }
}
