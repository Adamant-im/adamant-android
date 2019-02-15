package im.adamant.android.ui.presenters;


import com.arellomobile.mvp.InjectViewState;

import java.math.BigDecimal;

import im.adamant.android.BuildConfig;
import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.entities.Account;
import im.adamant.android.helpers.BalanceConvertHelper;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.SaveKeypairInteractor;
import im.adamant.android.interactors.SwitchPushNotificationServiceInteractor;
import im.adamant.android.ui.mvp_view.SettingsView;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class SettingsPresenter extends  BasePresenter<SettingsView> {
    private Router router;
    private SaveKeypairInteractor saveKeypairInteractor;
    private SwitchPushNotificationServiceInteractor switchPushNotificationServiceInteractor;
    private AdamantApiWrapper api;

    public SettingsPresenter(
            Router router,
            AdamantApiWrapper api,
            SaveKeypairInteractor saveKeypairInteractor,
            SwitchPushNotificationServiceInteractor switchPushNotificationServiceInteractor,
            CompositeDisposable subscriptions
    ) {
        super(subscriptions);
        this.router = router;
        this.api = api;
        this.saveKeypairInteractor = saveKeypairInteractor;
        this.switchPushNotificationServiceInteractor = switchPushNotificationServiceInteractor;
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
        getViewState().displayCurrentNotificationFacade(
                switchPushNotificationServiceInteractor.getCurrentFacade()
        );
    }

    public void onSetCheckedStoreKeypair(boolean value) {
        getViewState().startProgress();
        getViewState().setEnableStoreKeyPairOption(false);
        Disposable subscribe = saveKeypairInteractor.getFlowable()
                .subscribe(
                        (irrelevant) -> {
                            getViewState().setEnablePushOption(value);
                            getViewState().stopProgress();
                            getViewState().setEnableStoreKeyPairOption(true);
                            getViewState().showMessage(R.string.fragment_settings_success_saved);
                        },
                        (error) -> {
                            getViewState().stopProgress();
                            getViewState().setEnableStoreKeyPairOption(true);
                            getViewState().showMessage(error.getMessage());
                            LoggerHelper.e("saveKeyPair", error.getMessage(), error);
                        }
                );
        subscriptions.add(subscribe);

        saveKeypairInteractor.saveKeypair(value);
    }

    public void onClickShowSelectPushService() {
        router.navigateTo(Screens.PUSH_SUBSCRIPTION_SCREEN);
    }

//    public void onClickSaveSettings(Bundle config) {
//        if (config != null){
//            boolean isSaveKeypair = config.getBoolean(IS_SAVE_KEYPAIR, false);
//            boolean isSubscribeToNotifications = config.getBoolean(IS_RECEIVE_NOTIFICATIONS, false);
//
//            if (!isSaveKeypair) {isSubscribeToNotifications = false;}
//
//            saveKeyPair(isSaveKeypair);
//        }
//        getViewState().showSaveSettingsButton(false);
//    }

    public void onClickShowNodesList() {
        router.navigateTo(Screens.NODES_LIST_SCREEN);
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
