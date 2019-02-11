package im.adamant.android.ui.presenters;

import android.os.Bundle;
import android.webkit.URLUtil;

import com.arellomobile.mvp.InjectViewState;
import com.google.firebase.iid.FirebaseInstanceId;

import im.adamant.android.Screens;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.SaveKeypairInteractor;
import im.adamant.android.interactors.SubscribeToPushInteractor;
import im.adamant.android.ui.mvp_view.SettingsView;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

import static im.adamant.android.ui.mvp_view.SettingsView.IS_RECEIVE_NOTIFICATIONS;
import static im.adamant.android.ui.mvp_view.SettingsView.IS_SAVE_KEYPAIR;


@InjectViewState
public class SettingsPresenter extends  BasePresenter<SettingsView> {
    private Router router;
    private SaveKeypairInteractor saveKeypairInteractor;
    private SubscribeToPushInteractor subscribeToPushInteractor;

    public SettingsPresenter(
            Router router,
            SaveKeypairInteractor saveKeypairInteractor,
            SubscribeToPushInteractor subscribeToPushInteractor,
            CompositeDisposable subscriptions
    ) {
        super(subscriptions);
        this.router = router;
        this.saveKeypairInteractor = saveKeypairInteractor;
        this.subscribeToPushInteractor = subscribeToPushInteractor;
    }

    @Override
    public void attachView(SettingsView view) {
        super.attachView(view);
        getViewState().setStoreKeyPairOption(
                saveKeypairInteractor.isKeyPairMustBeStored()
        );
        getViewState().setEnablePushOption(
                saveKeypairInteractor.isKeyPairMustBeStored()
        );
        getViewState().switchPushOption(
                subscribeToPushInteractor.isEnabledPush()
        );
    }

    public void onSwitchStoreKeypair(boolean value) {
        getViewState().setEnablePushOption(value);
    }

    public void onClickSaveSettings(Bundle config){
        if (config != null){
            boolean isSaveKeypair = config.getBoolean(IS_SAVE_KEYPAIR, false);
            boolean isSubscribeToNotifications = config.getBoolean(IS_RECEIVE_NOTIFICATIONS, false);

            if (!isSaveKeypair) {isSubscribeToNotifications = false;}

            saveKeyPair(isSaveKeypair);
            savePushSettings(isSubscribeToNotifications);
        }
    }

    public void onClickShowNodesList() {
        router.navigateTo(Screens.NODES_LIST_SCREEN);
    }

    private void savePushSettings(boolean enable) {
        subscribeToPushInteractor.enablePush(enable);

        CompositeDisposable localSubscriptions = subscriptions;

        if (enable) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
                String deviceToken = instanceIdResult.getToken();
                Disposable subscribeToPush = subscribeToPushInteractor
                        .getEventsObservable()
                        .subscribe(
                                (event) -> {},
                                (error) -> LoggerHelper.e("savePushToken", error.getMessage(), error)
                        );
                localSubscriptions.add(subscribeToPush);

                subscribeToPushInteractor.savePushToken(deviceToken);
            });
        } else {
            Disposable unsubscribeFromPush = subscribeToPushInteractor
                    .getEventsObservable()
                    .subscribe(
                            (event) -> {},
                            (error) -> LoggerHelper.e("savePushToken", error.getMessage(), error)
                    );
            localSubscriptions.add(unsubscribeFromPush);

            subscribeToPushInteractor.deleteCurrentToken();
        }
    }

    private void saveKeyPair(boolean value) {
        Disposable subscribe = saveKeypairInteractor.getFlowable()
                .subscribe(
                        (irrelevant) -> {},
                        (error) -> LoggerHelper.e("saveKeyPair", error.getMessage(), error)
                );
        subscriptions.add(subscribe);

        saveKeypairInteractor.saveKeypair(value);
    }

}
