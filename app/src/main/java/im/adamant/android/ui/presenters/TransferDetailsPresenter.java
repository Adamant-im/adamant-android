package im.adamant.android.ui.presenters;

import android.os.Handler;

import com.arellomobile.mvp.InjectViewState;

import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.WalletInteractor;
import im.adamant.android.ui.mvp_view.TransferDetailsView;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class TransferDetailsPresenter extends ProtectedBasePresenter<TransferDetailsView> {
    private WalletInteractor walletInteractor;

    private String transactionId,currencyAbbr;

    public TransferDetailsPresenter(Router router, AccountInteractor accountInteractor,
                                    WalletInteractor walletInteractor) {
        super(router, accountInteractor);
        this.walletInteractor = walletInteractor;
    }

    //Called only once, right after onFirstViewAttach
    public void initParams(String transactionId,String currencyAbbr){
        this.transactionId = transactionId;
        this.currencyAbbr = currencyAbbr;
    }

    public void showExplorerClicked(){

    }

    public void chatClicked(){

    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().setLoading(true);
        new Handler().postDelayed(()->{
            getViewState().setLoading(false);
            getViewState().showTransferDetails(new TransferDetailsView.UITransferDetails()
            .setAmount("0.49 ADM")
            .setDate("May 15, 2018, 20:23")
            .setConfirmations(23)
            .setId(transactionId)
            .setStatus("Успешно")
            .setFromId("12344566162")
            .setFromAddress("Me")
            .setToId("44566162980")
            .setFee("0.5 ADM"));
        },3000);
    }
}
