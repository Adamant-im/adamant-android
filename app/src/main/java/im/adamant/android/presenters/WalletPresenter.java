package im.adamant.android.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

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
        getViewState().displayAdamantBalance(interactor.getAdamantBalance());
    }
}
