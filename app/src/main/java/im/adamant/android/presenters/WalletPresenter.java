package im.adamant.android.presenters;

import com.arellomobile.mvp.InjectViewState;

import java.math.BigDecimal;
import java.sql.Ref;
import java.util.concurrent.TimeUnit;

import im.adamant.android.Screens;
import im.adamant.android.core.AdamantApi;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.RefreshChatsInteractor;
import im.adamant.android.ui.mvp_view.WalletView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class WalletPresenter extends BasePresenter<WalletView> {
    private Router router;
    private AccountInteractor accountInteractor;
    private RefreshChatsInteractor refreshChatsInteractor;

    public WalletPresenter(
            Router router,
            AccountInteractor accountInteractor,
            RefreshChatsInteractor refreshChatsInteractor,
            CompositeDisposable subscription
    ) {
        super(subscription);
        this.accountInteractor = accountInteractor;
        this.refreshChatsInteractor = refreshChatsInteractor;
        this.router = router;
    }

    @Override
    public void attachView(WalletView view) {
        super.attachView(view);

        getViewState().displayAdamantAddress(accountInteractor.getAdamantAddress());

        Disposable subscribe = accountInteractor
                .getAdamantBalance()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext((balance) -> {
                    getViewState().displayAdamantBalance(balance);

                    //TODO: With such a check, there will be an error when the user who has spent money will be asked to receive more.
                    if (BigDecimal.ZERO.compareTo(balance) == 0){
                        getViewState().displayFreeTokenPageButton();
                    }
                })
                .doOnError((error) -> router.showSystemMessage(error.getMessage()))
                .repeatWhen((completed) -> completed.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS, TimeUnit.SECONDS))
                .subscribe();

        subscriptions.add(subscribe);


    }

    public void onClickGetFreeTokenButton() {
        router.navigateTo(WalletView.SHOW_FREE_TOKEN_PAGE, accountInteractor.getAdamantAddress());
    }

    public void onClickExitButton() {
        accountInteractor.logout();
        refreshChatsInteractor.cleanUp();
        router.navigateTo(Screens.LOGIN_SCREEN);
    }

}
