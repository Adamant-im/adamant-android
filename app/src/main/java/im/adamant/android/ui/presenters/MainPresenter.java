package im.adamant.android.ui.presenters;

import com.arellomobile.mvp.InjectViewState;

import im.adamant.android.Screens;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.SwitchPushNotificationServiceInteractor;
import im.adamant.android.interactors.chats.ChatInteractor;
import im.adamant.android.interactors.push.PushNotificationServiceFacade;
import im.adamant.android.ui.mvp_view.MainView;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class MainPresenter extends ProtectedBasePresenter<MainView> {
    private SwitchPushNotificationServiceInteractor pushNotificationServiceInteractor;
    private ChatInteractor chatInteractor;

    public MainPresenter(
            Router router,
            SwitchPushNotificationServiceInteractor pushNotificationServiceInteractor,
            AccountInteractor accountInteractor, ChatInteractor chatInteractor
            ) {
        super(router, accountInteractor);
        this.pushNotificationServiceInteractor = pushNotificationServiceInteractor;
        this.chatInteractor = chatInteractor;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        chatInteractor.startInitialLoading();

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

    public void goToFragment(String name) {
        switch (name) {
            case Screens.WALLET_SCREEN : {
                onSelectedWalletScreen();
            }
            break;
            case Screens.CHATS_SCREEN : {
                onSelectedChatsScreen();
            }
            break;
            case Screens.SETTINGS_SCREEN : {
                onSelectedSettingsScreen();
            }
            break;
        }
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
