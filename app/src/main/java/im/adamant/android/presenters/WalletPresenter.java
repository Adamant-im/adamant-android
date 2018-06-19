package im.adamant.android.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.math.BigDecimal;

import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.ui.mvp_view.WalletView;

@InjectViewState
public class WalletPresenter extends MvpPresenter<WalletView> {
    private AccountInteractor interactor;

    public WalletPresenter(AccountInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void attachView(WalletView view) {
        super.attachView(view);

        getViewState().displayAdamantAddress(interactor.getAdamantAddress());

        BigDecimal balance = interactor.getAdamantBalance();
        getViewState().displayAdamantBalance(balance);

        if (BigDecimal.ZERO.compareTo(balance) == 0){
            getViewState().displayShowFreeTokenPageButton();
        }
    }

    public void onClickGetFreeTokenButton(){
        getViewState().showFreeTokenPage(interactor.getAdamantAddress());
    }

    public void onClickJoinIcoButton(){
        getViewState().showJoinIcoPage(interactor.getAdamantAddress());
    }
}
