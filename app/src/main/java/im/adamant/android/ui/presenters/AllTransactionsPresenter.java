package im.adamant.android.ui.presenters;

import android.os.Bundle;

import com.arellomobile.mvp.InjectViewState;

import java.util.concurrent.TimeUnit;

import im.adamant.android.Screens;
import im.adamant.android.core.AdamantApi;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.WalletInteractor;
import im.adamant.android.ui.TransferDetailsScreen;
import im.adamant.android.ui.entities.CurrencyTransferEntity;
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


    private boolean loading = false;

    public void onShowTransactionsByCurrencyAbbr(String abbr) {

        currentOffset = 0;
        currentAbbr = abbr;
        loading = true;
        getViewState().setLoading(true);

        Disposable disposable = walletInteractor
                .getLastTransfersByCurrencyAbbr(abbr)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess((transfers) -> {
                    currentOffset += transfers.size();
                    loading = false;
                    getViewState().setLoading(false);
                    getViewState().firstTransfersWasLoaded(transfers);
                })
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
                                    .doOnNext(transfer -> {
                                        currentOffset++;
                                        getViewState().newTransferWasLoaded(transfer);
                                    })
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
        if (loading) {
            return;
        }
        getViewState().setLoading(true);
        loading = true;

        Disposable disposable = walletInteractor
                .getNextTransfersByCurrencyAbbr(currentAbbr, currentOffset)
                .observeOn(AndroidSchedulers.mainThread())
                .retry()
                .doOnComplete(() -> {
                    loading = false;
                    getViewState().setLoading(false);
                })
                .subscribe(
                        (currencyTransferEntity -> {
                            currentOffset++;
                            getViewState().nextTransferWasLoaded(currencyTransferEntity);
                        })
                );
        subscriptions.add(disposable);
    }

    public void onTransactionClicked(CurrencyTransferEntity currencyTransferEntity){
        Bundle bundle = new Bundle();
        bundle.putString(TransferDetailsScreen.ARG_TRANSFER_ID_KEY, currencyTransferEntity.getId());
        bundle.putString(TransferDetailsScreen.ARG_CURRENCY_ABBR, currentAbbr);
        router.navigateTo(Screens.TRANSFER_DETAILS_SCREEN, bundle);
    }
}
