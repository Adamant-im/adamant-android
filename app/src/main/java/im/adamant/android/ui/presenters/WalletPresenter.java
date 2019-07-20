package im.adamant.android.ui.presenters;

import android.os.Bundle;

import com.arellomobile.mvp.InjectViewState;

import java.util.concurrent.TimeUnit;

import im.adamant.android.Screens;
import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.WalletInteractor;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
import im.adamant.android.ui.AllTransactionsScreen;
import im.adamant.android.ui.TransferDetailsScreen;
import im.adamant.android.ui.entities.CurrencyCardItem;
import im.adamant.android.ui.entities.CurrencyTransferEntity;
import im.adamant.android.ui.mvp_view.WalletView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class WalletPresenter extends ProtectedBasePresenter<WalletView> {
    private WalletInteractor walletInteractor;
    private Disposable lastTransfersSubscription;
    private Disposable walletCardsSubscription;
    private CurrencyCardItem currencyCardItem;

    public WalletPresenter(
            Router router,
            AccountInteractor accountInteractor,
            WalletInteractor walletInteractor
    ) {
        super(router, accountInteractor);
        this.walletInteractor = walletInteractor;
    }

    @Override
    public void attachView(WalletView view) {
        super.attachView(view);

        if (walletCardsSubscription != null) {
            walletCardsSubscription.dispose();
        }

        walletCardsSubscription = walletInteractor
                .getCurrencyItemCards()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext((cards) -> getViewState().showCurrencyCards(cards))
                .doOnError((error) -> router.showSystemMessage(error.getMessage()))
                .retryWhen((retryHandler) -> retryHandler.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS, TimeUnit.SECONDS))
                .repeatWhen((completed) -> completed.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS, TimeUnit.SECONDS))
                .subscribe();

    }

    public void onSelectCurrencyCard(CurrencyCardItem cardItem){

        getViewState().startTransfersLoad();

        if (lastTransfersSubscription != null) {
            lastTransfersSubscription.dispose();
        }

        this.currencyCardItem = cardItem;

        lastTransfersSubscription = walletInteractor
                .getLastTransfersByCurrencyAbbr(cardItem.getAbbreviation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess((transfers) -> getViewState().showLastTransfers(transfers))
                .doOnError((error) -> {
                    //TODO: Create general logout control subsystem
                    if (error instanceof NotAuthorizedException){
                        router.navigateTo(Screens.SPLASH_SCREEN);
                    } else {
                        router.showSystemMessage(error.getMessage());
                    }
                })
                .retryWhen((retryHandler) -> retryHandler.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS, TimeUnit.SECONDS))
                .repeatWhen((completed) -> completed.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS, TimeUnit.SECONDS))
                .subscribe();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        onStopTransfersUpdate();
    }

    public void onStopTransfersUpdate() {
        if (lastTransfersSubscription != null){
            lastTransfersSubscription.dispose();
            lastTransfersSubscription = null;
        }

        if (walletCardsSubscription != null){
            walletCardsSubscription.dispose();
            walletCardsSubscription = null;
        }
    }

    public void onClickCopyCurrentCardAddress() {
        if (this.currencyCardItem != null){
            getViewState().putAddressToClipboard(this.currencyCardItem.getAddress());
        }
    }

    public void onClickCreateQrCodeCurrentCardAddress() {
        if (this.currencyCardItem != null){
            String address = this.currencyCardItem.getAddress();
            if (currencyCardItem.getAbbreviation().equalsIgnoreCase(SupportedWalletFacadeType.ADM.name())){
                address = "adm:" + address;
            }
            getViewState().createQrCode(address);
        }
    }

    public void onClickShowAllTransfers() {
        Bundle bundle = new Bundle();
        bundle.putString(AllTransactionsScreen.ARG_CURRENCY_ABBR, currencyCardItem.getAbbreviation());

        router.navigateTo(Screens.ALL_TRANSACTIONS_SCREEN, bundle);
    }

    public void onTransactionClicked(CurrencyTransferEntity currencyTransferEntity){
        Bundle bundle = new Bundle();
        bundle.putString(TransferDetailsScreen.ARG_TRANSFER_ID_KEY, currencyTransferEntity.getId());
        bundle.putString(TransferDetailsScreen.ARG_CURRENCY_ABBR, currencyTransferEntity.getCurrencyAbbreviation());
        router.navigateTo(Screens.TRANSFER_DETAILS_SCREEN, bundle);
    }

}
