package im.adamant.android.ui.presenters;

import com.arellomobile.mvp.InjectViewState;

import java.util.concurrent.TimeUnit;

import im.adamant.android.core.AdamantApi;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.TransferDetailsInteractor;
import im.adamant.android.ui.mvp_view.TransferDetailsView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class TransferDetailsPresenter extends ProtectedBasePresenter<TransferDetailsView> {
    private TransferDetailsInteractor interactor;

    private String transactionId, currencyAbbr;

    public TransferDetailsPresenter(Router router, AccountInteractor accountInteractor,
                                    TransferDetailsInteractor interactor) {
        super(router, accountInteractor);
        this.interactor = interactor;
    }

    private TransferDetailsView.UITransferDetails uiTransferDetails;

    //Called only once, right after onFirstViewAttach
    //Used instead of onFirstViewAttach
    public void initParams(String transactionId, String currencyAbbr) {
        this.transactionId = transactionId;
        this.currencyAbbr = currencyAbbr;
        getViewState().setLoading(true);
        subscriptions.add(interactor.getTransferDetailsInteractor(transactionId, currencyAbbr)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(uiDetails -> {
                    uiTransferDetails = uiDetails;
                    getViewState().setLoading(false);
                    getViewState().showTransferDetails(uiDetails);
                })
                .doOnError(throwable -> {
                    LoggerHelper.e(getClass().getSimpleName(), throwable.getMessage(), throwable);
                    router.showSystemMessage(throwable.getMessage());
                })
                .retryWhen((retryHandler) -> retryHandler.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS, TimeUnit.SECONDS))
                .repeatWhen((completed) -> completed.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS, TimeUnit.SECONDS))
                .subscribe());
    }

    public void showExplorerClicked() {
        if (uiTransferDetails != null) {
            getViewState().openBrowser(uiTransferDetails.getExplorerLink());
        }
    }

    public void chatClicked() {

    }

}
