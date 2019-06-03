package im.adamant.android.ui.presenters;

import com.arellomobile.mvp.InjectViewState;

import im.adamant.android.Screens;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.LogoutInteractor;
import im.adamant.android.interactors.SwitchPushNotificationServiceInteractor;
import im.adamant.android.interactors.push.PushNotificationServiceFacade;
import im.adamant.android.ui.mvp_view.MainView;
import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class MainPresenter extends ProtectedBasePresenter<MainView> {
    private SwitchPushNotificationServiceInteractor pushNotificationServiceInteractor;

    private String currentWindowCode = Screens.WALLET_SCREEN;

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
    }

    @Override
    public void attachView(MainView view) {
        super.attachView(view);
        switch (currentWindowCode){
            case Screens.WALLET_SCREEN: {
                getViewState().showWalletScreen();
            }
            break;
            case Screens.CHATS_SCREEN: {
                getViewState().showChatsScreen();
            }
            break;
            case Screens.SETTINGS_SCREEN: {
                getViewState().showSettingsScreen();
            }
            break;
        }
    }

    public void onSelectedWalletScreen() {
        currentWindowCode = Screens.WALLET_SCREEN;
        getViewState().showWalletScreen();
    }

    public void onSelectedChatsScreen() {
        currentWindowCode = Screens.CHATS_SCREEN;
        getViewState().showChatsScreen();
    }

    public void onSelectedSettingsScreen() {
        currentWindowCode = Screens.SETTINGS_SCREEN;
        getViewState().showSettingsScreen();
    }
}
