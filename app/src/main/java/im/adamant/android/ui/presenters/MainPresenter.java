package im.adamant.android.ui.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import im.adamant.android.Screens;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.LogoutInteractor;
import im.adamant.android.interactors.RefreshChatsInteractor;
import im.adamant.android.ui.mvp_view.MainView;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class MainPresenter extends BasePresenter<MainView> {
    private Router router;
    private LogoutInteractor logoutInteractor;

    private String currentWindowCode = Screens.WALLET_SCREEN;

    public MainPresenter(
            Router router,
            LogoutInteractor logoutInteractor,
            CompositeDisposable compositeDisposable
    ) {
        super(compositeDisposable);
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
        Disposable subscribe = logoutInteractor
                .execute()
                .subscribe(
                    (irrelevant) -> {
                        //TODO: Подписка неактуальна на момент срабатывания
                        router.navigateTo(Screens.LOGIN_SCREEN);
                    },
                    (error) -> {
                        router.showSystemMessage(error.getMessage());
                    }
                );

        subscriptions.add(subscribe);
    }
}
