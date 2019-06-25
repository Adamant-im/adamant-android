package im.adamant.android.ui.presenters;

import com.arellomobile.mvp.InjectViewState;

import java.util.concurrent.TimeUnit;

import im.adamant.android.core.AdamantApi;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.WalletInteractor;
import im.adamant.android.ui.mvp_view.AllTransactionsView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class AllTransactionsPresenter extends ProtectedBasePresenter<AllTransactionsView> {
    private String currentAbbr;
    private WalletInteractor walletInteractor;
    private int currentOffset = 0;

    public AllTransactionsPresenter(
            Router router,
            AccountInteractor accountInteractor,
            WalletInteractor walletInteractor
    ) {
        super(router, accountInteractor);
        this.walletInteractor = walletInteractor;
    }

    public void onShowTransactionsByCurrencyAbbr(String abbr) {

        currentAbbr = abbr;

        Disposable disposable = walletInteractor
                .getLastTransfersByCurrencyAbbr(abbr)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess((transfers) -> getViewState().firstTransfersWasLoaded(transfers))
                .doOnError(throwable -> {
                    LoggerHelper.e(getClass().getSimpleName(), throwable.getMessage(), throwable);
                    router.showSystemMessage(throwable.getMessage());
                })
                .retryWhen((retryHandler) -> retryHandler.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS, TimeUnit.SECONDS))
                .subscribe(
                        (transfers) -> {
                            Disposable subscribe = walletInteractor
                                    .getNewTransfersByCurrencyAbbr(abbr)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnNext(transfer -> getViewState().newTransferWasLoaded(transfer))
                                    .doOnError(throwable -> {
                                        LoggerHelper.e(getClass().getSimpleName(), throwable.getMessage(), throwable);
                                        router.showSystemMessage(throwable.getMessage());
                                    })
                                    .retryWhen((retryHandler) -> retryHandler.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS, TimeUnit.SECONDS))
                                    .repeatWhen((completed) -> completed.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS, TimeUnit.SECONDS))
                                    .subscribe();
                            subscriptions.add(subscribe);
                        }
                );

        subscriptions.add(disposable);
    }

    public void onLoadNextTransfers() {

    }
}
