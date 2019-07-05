package im.adamant.android.ui.presenters;


import android.os.Bundle;

import com.arellomobile.mvp.InjectViewState;

import java.math.BigDecimal;

import im.adamant.android.BuildConfig;
import im.adamant.android.Screens;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.entities.Account;
import im.adamant.android.helpers.BalanceConvertHelper;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.LogoutInteractor;
import im.adamant.android.interactors.SecurityInteractor;
import im.adamant.android.interactors.SwitchPushNotificationServiceInteractor;
import im.adamant.android.ui.mvp_view.PinCodeView;
import im.adamant.android.ui.mvp_view.SettingsView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class SettingsPresenter extends ProtectedBasePresenter<SettingsView> {
    private SecurityInteractor securityInteractor;
    private LogoutInteractor logoutInteractor;
    private Disposable logoutDisposable;
    private SwitchPushNotificationServiceInteractor switchPushNotificationServiceInteractor;
    private AdamantApiWrapper api;

    public SettingsPresenter(
            Router router,
            AccountInteractor accountInteractor,
            LogoutInteractor logoutInteractor,
            AdamantApiWrapper api,
            SecurityInteractor securityInteractor,
            SwitchPushNotificationServiceInteractor switchPushNotificationServiceInteractor
    ) {
        super(router, accountInteractor);
        this.api = api;
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

    private boolean storeKeyPairViewValue;
    private boolean checkingTEE;

    private void setCheckingTEE(boolean checkingTEE) {
        this.checkingTEE = checkingTEE;
        getViewState().setEnableStoreKeyPairOption(!checkingTEE);
    }

    public void onSecurityCheckResult(boolean hardwareSecuredDevice) {
        setCheckingTEE(false);
        if (!hardwareSecuredDevice) {
            getViewState().showTEENotSupportedDialog();
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable(PinCodeView.ARG_MODE, storeKeyPairViewValue ? PinCodeView.MODE.CREATE : PinCodeView.MODE.DROP);
            router.navigateTo(Screens.PINCODE_SCREEN, bundle);
        }
    }

    public void onSetCheckedStoreKeypair(boolean value, boolean isUserConfirmed) {
        if (checkingTEE) {
            return;
        }
        storeKeyPairViewValue = value;
        if (value != securityInteractor.isKeyPairMustBeStored()) {

            if (value && !isUserConfirmed) {
                setCheckingTEE(true);
                Disposable disposable = securityInteractor.isHardwareSecuredDevice()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.computation())
                        .subscribe(this::onSecurityCheckResult);
                subscriptions.add(disposable);
            } else {
                Bundle bundle = new Bundle();
                bundle.putSerializable(PinCodeView.ARG_MODE, (value) ? PinCodeView.MODE.CREATE : PinCodeView.MODE.DROP);
                router.navigateTo(Screens.PINCODE_SCREEN, bundle);
            }
        }
    }

    public void onClickShowSelectPushService() {
        router.navigateTo(Screens.PUSH_SUBSCRIPTION_SCREEN);
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
