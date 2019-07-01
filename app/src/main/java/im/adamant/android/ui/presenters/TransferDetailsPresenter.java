package im.adamant.android.ui.presenters;

import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.WalletInteractor;
import im.adamant.android.ui.mvp_view.TransferDetailsView;
import ru.terrakok.cicerone.Router;

public class TransferDetailsPresenter extends ProtectedBasePresenter<TransferDetailsView> {
    private WalletInteractor walletInteractor;

    private String transactionId,currencyAbbr;

    public TransferDetailsPresenter(Router router, AccountInteractor accountInteractor,
                                    WalletInteractor walletInteractor) {
        super(router, accountInteractor);
        this.walletInteractor = walletInteractor;
    }

    public TransferDetailsPresenter setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public TransferDetailsPresenter setCurrencyAbbr(String currencyAbbr) {
        this.currencyAbbr = currencyAbbr;
        return this;
    }
}
