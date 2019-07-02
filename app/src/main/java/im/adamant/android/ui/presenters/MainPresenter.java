package im.adamant.android.ui.presenters;

import android.os.Bundle;

import com.arellomobile.mvp.InjectViewState;

import im.adamant.android.Screens;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.SwitchPushNotificationServiceInteractor;
import im.adamant.android.interactors.push.PushNotificationServiceFacade;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
import im.adamant.android.ui.TransferDetailsScreen;
import im.adamant.android.ui.mvp_view.MainView;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class MainPresenter extends ProtectedBasePresenter<MainView> {
    private SwitchPushNotificationServiceInteractor pushNotificationServiceInteractor;

    public MainPresenter(
            Router router,
            SwitchPushNotificationServiceInteractor pushNotificationServiceInteractor,
            AccountInteractor accountInteractor
    ) {
        super(router, accountInteractor);
        this.pushNotificationServiceInteractor = pushNotificationServiceInteractor;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        Bundle bundle = new Bundle();
        bundle.putString(TransferDetailsScreen.TRANSFER_ID_KEY,"4529855394592563626");
        bundle.putString(TransferDetailsScreen.CURRENCY_ABBR, SupportedWalletFacadeType.ADM.toString());
        router.navigateTo(Screens.TRANSFER_DETAILS_SCREEN, bundle);
        PushNotificationServiceFacade currentFacade = pushNotificationServiceInteractor.getCurrentFacade();
        if (currentFacade != null) {
            Disposable pushSubscription = currentFacade
                    .subscribe()
                    .subscribe(
                            () -> {},
                            (error) -> {
                                LoggerHelper.e(getClass().getSimpleName(), error.getMessage(), error);
                            }
                    );
            subscriptions.add(pushSubscription);
        }

        onSelectedWalletScreen();
    }


    public void onSelectedWalletScreen() {
        getViewState().showWalletScreen();
    }

    public void onSelectedChatsScreen() {
        getViewState().showChatsScreen();
    }

    public void onSelectedSettingsScreen() {
        getViewState().showSettingsScreen();
    }
}
