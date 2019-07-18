package im.adamant.android.ui.presenters;


import android.os.Bundle;

import com.arellomobile.mvp.InjectViewState;

import java.math.BigDecimal;
import java.util.ArrayList;

import im.adamant.android.BuildConfig;
import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.entities.Account;
import im.adamant.android.helpers.BalanceConvertHelper;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.LogoutInteractor;
import im.adamant.android.interactors.SecurityInteractor;
import im.adamant.android.interactors.SwitchPushNotificationServiceInteractor;
import im.adamant.android.interactors.push.PushNotificationServiceFacade;
import im.adamant.android.ui.mvp_view.PinCodeView;
import im.adamant.android.ui.mvp_view.SettingsView;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class SettingsPresenter extends ProtectedBasePresenter<SettingsView> {
    private SecurityInteractor securityInteractor;
    private LogoutInteractor logoutInteractor;
    private Disposable logoutDisposable;
    private SwitchPushNotificationServiceInteractor switchPushNotificationServiceInteractor;
    private AdamantApiWrapper api;
    private Scheduler observeScheduler;

    public SettingsPresenter(
            Router router,
            AccountInteractor accountInteractor,
            LogoutInteractor logoutInteractor,
            AdamantApiWrapper api,
            SecurityInteractor securityInteractor,
            SwitchPushNotificationServiceInteractor switchPushNotificationServiceInteractor,
            Scheduler observeScheduler
    ) {
        super(router, accountInteractor);
        this.api = api;
        this.observeScheduler = observeScheduler;
        this.securityInteractor = securityInteractor;
        this.logoutInteractor = logoutInteractor;
        this.switchPushNotificationServiceInteractor = switchPushNotificationServiceInteractor;
    }

    @Override
    public void attachView(SettingsView view) {
        super.attachView(view);

        getViewState().setCheckedStoreKeyPairOption(
                securityInteractor.isKeyPairMustBeStored()
        );

        //TODO: Check minimum balance must be move to FCMPushServiceFacade
        getViewState().setEnablePushOption(
                securityInteractor.isKeyPairMustBeStored() && isHaveMinimumBalance()
        );
        getViewState().displayCurrentNotificationFacade(
                switchPushNotificationServiceInteractor.getCurrentFacade()
        );
    }

    public void onSetCheckedStoreKeypair(boolean value, boolean isUserConfirmed) {
        if (value != securityInteractor.isKeyPairMustBeStored()) {

            if (value && !isUserConfirmed) {
                boolean hardwareSecuredDevice = securityInteractor.isHardwareSecuredDevice();

                if (!hardwareSecuredDevice) {
                    getViewState().showTEENotSupportedDialog();
                    return;
                }
            }

            if (value) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(PinCodeView.ARG_MODE, PinCodeView.MODE.CREATE);
                router.navigateTo(Screens.PINCODE_SCREEN, bundle);
            }else {
                securityInteractor.forceDropPassphrase();
            }
        }
    }

    public void onClickShowSelectPushService() {
        getViewState().showSelectServiceDialog(
                new ArrayList<>(switchPushNotificationServiceInteractor.getFacades().values()),
                switchPushNotificationServiceInteractor.getCurrentFacade());

        //router.navigateTo(Screens.PUSH_SUBSCRIPTION_SCREEN);
    }

    public void onClickShowNodesList() {
        router.navigateTo(Screens.NODES_LIST_SCREEN);
    }

    public void onClickShowExitDialogButton() {
        getViewState().showExitDialog();
    }

    public void onClickExitButton() {

        if (logoutDisposable != null) {
            logoutDisposable.dispose();
        }

        logoutDisposable = logoutInteractor
                .getEventBus()
                .subscribe(
                        (irrelevant) -> {
                            router.navigateTo(Screens.SPLASH_SCREEN);
                        },
                        (error) -> {
                            router.showSystemMessage(error.getMessage());
                        }
                );

        logoutInteractor.logout();
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (logoutDisposable != null) {
            logoutDisposable.dispose();
        }
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
