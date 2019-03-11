package im.adamant.android.ui.presenters;


import android.os.Bundle;

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
import im.adamant.android.interactors.SecurityInteractor;
import im.adamant.android.interactors.SwitchPushNotificationServiceInteractor;
import im.adamant.android.ui.mvp_view.PinCodeView;
import im.adamant.android.ui.mvp_view.SettingsView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class SettingsPresenter extends  BasePresenter<SettingsView> {
    private Router router;
    private SecurityInteractor securityInteractor;
    private SwitchPushNotificationServiceInteractor switchPushNotificationServiceInteractor;
    private AdamantApiWrapper api;

    public SettingsPresenter(
            Router router,
            AdamantApiWrapper api,
            SecurityInteractor securityInteractor,
            SwitchPushNotificationServiceInteractor switchPushNotificationServiceInteractor,
            CompositeDisposable subscriptions
    ) {
        super(subscriptions);
        this.router = router;
        this.api = api;
        this.securityInteractor = securityInteractor;
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

    public void onSetCheckedStoreKeypair(boolean value) {
        if (value != securityInteractor.isKeyPairMustBeStored()) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(PinCodeView.ARG_MODE, PinCodeView.MODE.CREATE);
            router.navigateTo(Screens.PINCODE_SCREEN, bundle);
        }
    }

    public void onClickShowSelectPushService() {
        router.navigateTo(Screens.PUSH_SUBSCRIPTION_SCREEN);
    }

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
