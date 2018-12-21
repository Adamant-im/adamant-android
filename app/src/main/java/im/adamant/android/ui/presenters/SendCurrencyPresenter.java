package im.adamant.android.ui.presenters;

import com.arellomobile.mvp.InjectViewState;

import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.SendCurrencyInteractor;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
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
    }

    public void onClickShowInterfaceFor(String recipientAddress) {
//        Disposable subscribe = sendCurrencyInteractor
//                .getAvailableCurrencies(recipientAddress)
//                .observeOn(AndroidSchedulers.mainThread())
//                .toList()
//                .subscribe(
//                        list -> getViewState().showSendCurrencyInterface(list),
//                        error -> LoggerHelper.e("Send Currency", error.getMessage(), error)
//                );
//
//        subscriptions.add(subscribe);
    }
}
