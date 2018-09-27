package im.adamant.android.presenters;

import com.arellomobile.mvp.InjectViewState;

import java.util.concurrent.TimeUnit;

import im.adamant.android.core.AdamantApi;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.ui.entities.CurrencyCardItem;
import im.adamant.android.ui.mvp_view.WalletView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class WalletPresenter extends BasePresenter<WalletView> {
    private Router router;
    private AccountInteractor accountInteractor;
    private Disposable lastTransfersSubscription;

    public WalletPresenter(
            Router router,
            AccountInteractor accountInteractor,
            CompositeDisposable subscription
    ) {
        super(subscription);
        this.accountInteractor = accountInteractor;
        this.router = router;
    }

    @Override
    public void attachView(WalletView view) {
        super.attachView(view);

        Disposable subscribe = accountInteractor
                .getCurrencyItemCards()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext((cards) -> getViewState().showCurrencyCards(cards))
                .doOnError((error) -> router.showSystemMessage(error.getMessage()))
                .repeatWhen((completed) -> completed.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS, TimeUnit.SECONDS))
                .subscribe();

        subscriptions.add(subscribe);

    }

    public void onSelectCurrencyCard(CurrencyCardItem cardItem){

        if (lastTransfersSubscription != null) {
            lastTransfersSubscription.dispose();
        }

        lastTransfersSubscription = accountInteractor
                .getLastTransfersByCurrencyAbbr(cardItem.getAbbreviation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess((transfers) -> getViewState().showLastTransfers(transfers))
                .doOnError((error) -> router.showSystemMessage(error.getMessage()))
                .repeatWhen((completed) -> completed.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS, TimeUnit.SECONDS))
                .subscribe();
    }
}
