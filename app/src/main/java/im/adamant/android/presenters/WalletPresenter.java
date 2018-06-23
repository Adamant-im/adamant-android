package im.adamant.android.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.math.BigDecimal;

import im.adamant.android.Screens;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.ui.mvp_view.WalletView;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class WalletPresenter extends MvpPresenter<WalletView> {
    private Router router;
    private AccountInteractor interactor;

    public WalletPresenter(Router router, AccountInteractor interactor) {
        this.interactor = interactor;
        this.router = router;
    }

    @Override
    public void attachView(WalletView view) {
        super.attachView(view);

        getViewState().displayAdamantAddress(interactor.getAdamantAddress());

        BigDecimal balance = interactor.getAdamantBalance();
        getViewState().displayAdamantBalance(balance);

        if (BigDecimal.ZERO.compareTo(balance) == 0){
            getViewState().displayFreeTokenPageButton();
        }
    }

    public void onClickGetFreeTokenButton() {
        router.navigateTo(WalletView.SHOW_FREE_TOKEN_PAGE, interactor.getAdamantAddress());
    }

    public void onClickExitButton() {
        interactor.logout();
        router.navigateTo(Screens.LOGIN_SCREEN);
    }

}
