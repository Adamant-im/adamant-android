package im.adamant.android.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import im.adamant.android.Screens;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.RefreshChatsInteractor;
import im.adamant.android.ui.mvp_view.MainView;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {
    private Router router;
    private CompositeDisposable compositeDisposable;
    private AccountInteractor accountInteractor;
    private RefreshChatsInteractor refreshChatsInteractor;

    private String currentWindowCode = Screens.WALLET_SCREEN;

    public MainPresenter(
            Router router,
            AccountInteractor accountInteractor,
            RefreshChatsInteractor refreshChatsInteractor,
            CompositeDisposable compositeDisposable
    ) {
        this.router = router;
        this.compositeDisposable = compositeDisposable;
        this.accountInteractor = accountInteractor;
        this.refreshChatsInteractor = refreshChatsInteractor;
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

    @Override
    public void detachView(MainView view) {
        super.detachView(view);
        compositeDisposable.dispose();
        compositeDisposable.clear();
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
        accountInteractor.logout();
        refreshChatsInteractor.cleanUp();
        router.navigateTo(Screens.LOGIN_SCREEN);
    }
}
