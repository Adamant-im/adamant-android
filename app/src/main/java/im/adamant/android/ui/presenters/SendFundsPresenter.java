package im.adamant.android.ui.presenters;

import com.arellomobile.mvp.InjectViewState;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import im.adamant.android.BuildConfig;
import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.helpers.AdamantAddressValidateHelper;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.chats.ChatsStorage;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.helpers.PublicKeyStorage;
import im.adamant.android.interactors.SendFundsInteractor;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
import im.adamant.android.interactors.wallets.WalletFacade;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.mvp_view.SendFundsView;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class SendFundsPresenter extends ProtectedBasePresenter<SendFundsView> {
    private Map<SupportedWalletFacadeType, WalletFacade> wallets;
    private SendFundsInteractor sendCurrencyInteractor;
    private PublicKeyStorage publicKeyStorage;
    private ChatsStorage chatsStorage;

    private String companionId;
    private SupportedWalletFacadeType facadeType;
    private WalletFacade currentFacade;

    private BigDecimal currentAmount = BigDecimal.ZERO;
    private String comment = "";

    public SendFundsPresenter(
            Router router,
            AccountInteractor accountInteractor,
            Map<SupportedWalletFacadeType, WalletFacade> wallets,
            SendFundsInteractor sendCurrencyInteractor,
            PublicKeyStorage publicKeyStorage,
            ChatsStorage chatsStorage
    ) {
        super(router, accountInteractor);
        this.wallets = wallets;
        this.sendCurrencyInteractor = sendCurrencyInteractor;
        this.publicKeyStorage = publicKeyStorage;
        this.chatsStorage = chatsStorage;
    }

    public void setCompanionIdAndFacadeType(String companionId, SupportedWalletFacadeType type) {
        this.companionId = companionId;
        this.facadeType = type;

        currentFacade = wallets.get(type);

        getViewState().setFundsSendingIsSupported(
                currentFacade.isSupportFundsSending()
        );

        if (companionId != null) {
            Disposable subscription = publicKeyStorage
                    .findPublicKey(companionId)
                    .map(publicKey -> currentFacade.getCurrencyAddress(companionId, publicKey))
                    .subscribe(
                            address -> getViewState().setRecipientAddress(address),
                            error -> LoggerHelper.e(getClass().getSimpleName(), error.getMessage(), error)
                    );

            subscriptions.add(subscription);
        }


        if (currentFacade.isSupportComment()){
            getViewState().showCommentField();
        } else {
            getViewState().hideCommentField();
        }

        int resourceId = currentFacade.getIconForEditText();
        if (resourceId != 0) {
            getViewState().setEditTextCurrencyIcons(resourceId);
        }

        if (companionId == null) {
            getViewState().unlockRecipientAddress();
        } else {
            getViewState().lockRecipientAddress();
            Chat chat = chatsStorage.findChatByCompanionId(companionId);
            if (chat != null) {
                getViewState().setRecipientName(chat.getTitle());
            }
        }

        Disposable balanceUpdateFlowable = Flowable
                .interval(0, BuildConfig.UPDATE_BALANCE_SECONDS_DELAY, TimeUnit.SECONDS)
                .withLatestFrom(currentFacade.getFee(), (i, fee) -> fee)
                .subscribe(
                        fee -> {
                            BigDecimal balance = currentFacade.getBalance();
                            calculate(currentAmount, balance, fee);
                        },
                        error -> LoggerHelper.e("UPDATED BALANCE", error.getMessage(), error)
                );
        subscriptions.add(balanceUpdateFlowable);

    }

    public void onInputAmount(CharSequence amountString) {
        BigDecimal amount = getAmountFromString(amountString);

        if (amount != null) {
            currentAmount = amount;
            Disposable subscribe = currentFacade
                    .getFee()
                    .subscribe(
                            fee -> calculate(currentAmount, currentFacade.getBalance(), fee),
                            error -> LoggerHelper.e("amount", error.getMessage(), error)
                    );
            subscriptions.add(subscribe);
        }
    }

    public void onInputRecipientAddress(String address, CharSequence amountString) {
        BigDecimal amount = getAmountFromString(amountString);

        if (AdamantAddressValidateHelper.validate(address)) {
            getViewState().dropRecipientAddressError();
            getViewState().setRecipientName(address);
            this.companionId = address;

        } else {
//            getViewState().showRecipientAddressError(R.string.wrong_address);
            this.companionId = null;
        }

        Disposable subscribe = currentFacade
                .getFee()
                .subscribe(
                        fee -> calculate(amount, currentFacade.getBalance(), fee),
                        error -> LoggerHelper.e("amount", error.getMessage(), error)
                );
        subscriptions.add(subscribe);
    }

    public void onClickSendButton() {
        if (companionId == null) {
            getViewState().showRecipientAddressError(R.string.wrong_address);
            return;
        }

        if (currentAmount == null || currentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            getViewState().showAmountError(R.string.not_a_number);
            return;
        }

        getViewState().showTransferConfirmationDialog(currentAmount, facadeType.name(), companionId);
    }

    public void onClickConfirmSend() {
        if (companionId != null) {
            getViewState().lockSendButton();
            Disposable subscribe = sendCurrencyInteractor
                    .sendCurrency(companionId, comment, currentAmount, facadeType)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            transactionWasProcessed -> {
                                companionId = null;
                                currentAmount = null;
                                getViewState().unlockSendButton();
                                router.backTo(Screens.MESSAGES_SCREEN);
                            },
                            error -> {
                                getViewState().unlockSendButton();
                                router.showSystemMessage(error.getMessage());
                            }
                    );

            subscriptions.add(subscribe);
        } else {
            getViewState().showRecipientAddressError(R.string.wrong_address);
        }
    }

    private void calculate(BigDecimal amount, BigDecimal balance, BigDecimal fee) {

        getViewState().setFee(fee, facadeType.name());

        BigDecimal totalAmount = amount.add(fee);
        getViewState().setTotalAmount(totalAmount, facadeType.name());

        getViewState().setCurrentBalance(balance, facadeType.name());

        BigDecimal reminder = balance.subtract(totalAmount);
        getViewState().setReminder(reminder, facadeType.name());

//        boolean isSendButtonMustLocked = (
//                    (reminder.compareTo(BigDecimal.ZERO) < 0) ||
//                    (amount.compareTo(BigDecimal.ZERO) <= 0) ||
//                    (companionId == null)
//        );
//
//        if (isSendButtonMustLocked) {
//            getViewState().lockSendButton();
//        } else {
//            getViewState().unlockSendButton();
//        }
    }

    private BigDecimal getAmountFromString(CharSequence amountString) {
        BigDecimal amount = null;
        try {
            amount = new BigDecimal(amountString.toString());
            getViewState().dropAmountError();
        } catch (Exception e) {
            getViewState().showAmountError(R.string.not_a_number);
            LoggerHelper.e(getClass().getSimpleName(), e.getMessage(), e);
        }

        return amount;
    }
}
