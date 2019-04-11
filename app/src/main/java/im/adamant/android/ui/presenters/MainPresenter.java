package im.adamant.android.ui.presenters;

import com.arellomobile.mvp.InjectViewState;

import im.adamant.android.Screens;
import im.adamant.android.interactors.LogoutInteractor;
import im.adamant.android.ui.mvp_view.MainView;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class MainPresenter extends BasePresenter<MainView> {
    private Router router;
    private LogoutInteractor logoutInteractor;
    private Disposable logoutDisposable;

    private String currentWindowCode = Screens.WALLET_SCREEN;

    public MainPresenter(
            Router router,
            LogoutInteractor logoutInteractor
    ) {
        this.router = router;
        this.logoutInteractor = logoutInteractor;
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

    public void onClickExitButton() {

        if (logoutDisposable != null) {
            logoutDisposable.dispose();
        }

        logoutDisposable = logoutInteractor
                .getEventBus()
                .subscribe(
                    (irrelevant) -> {
                        router.navigateTo(Screens.LOGIN_SCREEN);
                    },
                    (error) -> {
                        router.showSystemMessage(error.getMessage());
                    }
                );

        logoutInteractor.execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (logoutDisposable != null) {
            logoutDisposable.dispose();
        }
    }
}
