package im.adamant.android.ui.presenters;

import android.os.Bundle;

import com.arellomobile.mvp.InjectViewState;
import com.google.firebase.iid.FirebaseInstanceId;

import java.math.BigDecimal;

import im.adamant.android.BuildConfig;
import im.adamant.android.Screens;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.entities.Account;
import im.adamant.android.helpers.BalanceConvertHelper;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.SaveKeypairInteractor;
import im.adamant.android.interactors.SubscribeToFcmPushInteractor;
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
    private SubscribeToFcmPushInteractor subscribeToPushInteractor;
    private AdamantApiWrapper api;

    public SettingsPresenter(
            Router router,
            AdamantApiWrapper api,
            SaveKeypairInteractor saveKeypairInteractor,
            SubscribeToFcmPushInteractor subscribeToPushInteractor,
            CompositeDisposable subscriptions
    ) {
        super(subscriptions);
        this.router = router;
        this.api = api;
        this.saveKeypairInteractor = saveKeypairInteractor;
        this.subscribeToPushInteractor = subscribeToPushInteractor;
    }

    @Override
    public void attachView(SettingsView view) {
        super.attachView(view);

        getViewState().setCheckedStoreKeyPairOption(
                saveKeypairInteractor.isKeyPairMustBeStored()
        );
        getViewState().setEnablePushOption(
                saveKeypairInteractor.isKeyPairMustBeStored() && isHaveMinimumBalance()
        );
        getViewState().setCheckedPushOption(
                subscribeToPushInteractor.isEnabledPush()
        );
    }

    public void onSetCheckedStoreKeypair(boolean value) {
        getViewState().setEnablePushOption(value);
        getViewState().showSaveSettingsButton(true);
    }

    public void onSetCheckedPushOption(boolean value) {
        getViewState().setEnablePushOption(value);
        getViewState().showSaveSettingsButton(true);
    }

    public void onClickSaveSettings(Bundle config) {
        if (config != null){
            boolean isSaveKeypair = config.getBoolean(IS_SAVE_KEYPAIR, false);
            boolean isSubscribeToNotifications = config.getBoolean(IS_RECEIVE_NOTIFICATIONS, false);

            if (!isSaveKeypair) {isSubscribeToNotifications = false;}

            saveKeyPair(isSaveKeypair);
            savePushSettings(isSubscribeToNotifications);
        }
        getViewState().showSaveSettingsButton(false);
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

    private boolean isHaveMinimumBalance() {
        boolean result = false;
        Account account = api.getAccount();

        if (api.isAuthorized() && account != null) {
            BigDecimal balance = BalanceConvertHelper.convert(account.getBalance());
            int compareResult = balance.compareTo(BuildConfig.ADM_MINIMUM_COST);
            result = (compareResult >= 0);
        }

        return result;
    }

}
