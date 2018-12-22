package im.adamant.android.ui.presenters;

import com.arellomobile.mvp.InjectViewState;

import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.SendCurrencyInteractor;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
import im.adamant.android.interactors.wallets.WalletFacade;
import im.adamant.android.ui.mvp_view.SendCurrencyTransferView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class SendCurrencyPresenter extends BasePresenter<SendCurrencyTransferView> {
    private Router router;
    private SendCurrencyInteractor sendCurrencyInteractor;

    private String companionId;
    private SupportedWalletFacadeType facadeType;

    public SendCurrencyPresenter(Router router, SendCurrencyInteractor sendCurrencyInteractor, CompositeDisposable subscriptions) {
        super(subscriptions);
        this.router = router;
        this.sendCurrencyInteractor = sendCurrencyInteractor;
    }

    public void setCompanionIdAndFacadeType(String companionId, SupportedWalletFacadeType type) {
        this.companionId = companionId;
        this.facadeType = type;

        WalletFacade facade = sendCurrencyInteractor.getFacade(type);

        getViewState().setTransferIsSupported(
                facade.isSupportCurrencySending()
        );
    }
}
