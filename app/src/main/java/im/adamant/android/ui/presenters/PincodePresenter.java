package im.adamant.android.ui.presenters;

import com.arellomobile.mvp.InjectViewState;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import im.adamant.android.BuildConfig;
import im.adamant.android.R;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.SecurityInteractor;
import im.adamant.android.ui.mvp_view.PinCodeView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class PincodePresenter extends BasePresenter<PinCodeView> {
    private SecurityInteractor pinCodeInteractor;
    private PinCodeView.MODE mode = PinCodeView.MODE.ACCESS_TO_APP;
    private String pincodeForConfirmation;
    private int attemptsCount = 0;
    private long lastAttemptTimestamp = 0;
    private Disposable currentOperation;
    private Disposable timerErrorDisposable;

    public PincodePresenter(SecurityInteractor pinCodeInteractor, CompositeDisposable subscriptions) {
        super(subscriptions);
        this.pinCodeInteractor = pinCodeInteractor;
    }

    public void setMode(PinCodeView.MODE mode) {
        this.mode = mode;
        switch (mode){
            case CREATE: {
                getViewState().setSuggestion(R.string.activity_pincode_enter_new_pincode);
            }
            break;
            case ACCESS_TO_APP: {
                getViewState().setSuggestion(R.string.activity_pincode_enter_pincode);
            }
            break;
            case DROP: {
                getViewState().setSuggestion(R.string.activity_pincode_enter_pincode);
            }
            break;
        }
    }

    public void onInputPincodeWasCompleted(String pinCode) {
        if (!validate(pinCode)) {
            getViewState().dropPincodeText();
            return;
        }

        if (currentOperation != null) {
            currentOperation.dispose();
        }

        switch (mode){
            case CREATE: {
                mode = PinCodeView.MODE.CONFIRM;
                pincodeForConfirmation = pinCode;
                getViewState().setSuggestion(R.string.activity_pincode_confirm);
                getViewState().dropPincodeText();
            }
            break;
            case CONFIRM: {
                if (pinCode.equalsIgnoreCase(pincodeForConfirmation)){
                    getViewState().startProcess();
                    currentOperation = pinCodeInteractor
                            .savePassphrase(pinCode)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    () -> {
                                        getViewState().stopProcess(true);
                                        getViewState().close();
                                    },
                                    error -> {
                                        getViewState().stopProcess(false);
                                        getViewState().showError(R.string.encryption_error);
                                        LoggerHelper.e("PINCODE", error.getMessage(), error);
                                    }
                            );
                } else {
                    getViewState().showError(R.string.pincode_unconfirmed);
                    getViewState().setSuggestion(R.string.activity_pincode_enter_new_pincode);
                    mode = PinCodeView.MODE.CREATE;
                }

                getViewState().dropPincodeText();
                pincodeForConfirmation = null;

            }
            break;
            case ACCESS_TO_APP: {
                attemptsCount++;

                if (waitTimeOut()) { return; }

                lastAttemptTimestamp = System.currentTimeMillis();

                getViewState().startProcess();
                currentOperation = pinCodeInteractor
                        .restoreAuthorizationByPincode(pinCode)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                (authorization) -> {
                                    getViewState().stopProcess(true);
                                    if (authorization.isSuccess()) {
                                        getViewState().goToMain();
                                    } else {
                                        getViewState().showError(R.string.account_not_found);
                                    }
                                },
                                error -> {
                                    getViewState().stopProcess(false);
                                    getViewState().showError(R.string.wrong_pincode);
                                    getViewState().dropPincodeText();
                                    LoggerHelper.e("PINCODE", error.getMessage(), error);
                                }
                        );
            }
            break;
            case DROP: {
                getViewState().startProcess();
                currentOperation = pinCodeInteractor
                        .dropPassphrase(pinCode)
                        .doOnError((throwable -> {LoggerHelper.e("PINCODE", "after ignore UNSUCESS");}))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                (value) -> {
                                    LoggerHelper.e("PINCODE", "SUCCESS SUBSCRIBE");
                                    getViewState().stopProcess(true);
                                    getViewState().close();
                                },
                                error -> {
                                    getViewState().stopProcess(false);
                                    getViewState().showError(R.string.wrong_pincode);
                                    getViewState().dropPincodeText();
                                    LoggerHelper.e("PINCODE", error.getMessage(), error);
                                }
                        );
            }
            break;
        }
    }

    private boolean waitTimeOut() {
        if (attemptsCount > BuildConfig.MAX_WRONG_PINCODE_ATTEMTS) {
            if (lastAttemptTimestamp < (System.currentTimeMillis() - BuildConfig.WRONG_PINCODE_WAIT_MILISECONDS)) {
                attemptsCount = 0;
                return false;
            } else {
                lastAttemptTimestamp = System.currentTimeMillis();
                getViewState().dropPincodeText();
                getViewState().shuffleKeyboard();

                if (timerErrorDisposable != null) {
                    timerErrorDisposable.dispose();
                }

                int waitSeconds = BuildConfig.WRONG_PINCODE_WAIT_MILISECONDS / 1000;

                timerErrorDisposable = Observable
                        .interval(1, TimeUnit.SECONDS)
                        .take(waitSeconds)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                (second) -> {
                                    LoggerHelper.e("INTERVAL", Long.toString(second));
                                    getViewState().showRepeatableError(R.string.pincode_exceeding_the_number_of_attempts, waitSeconds - second.intValue());
                                },
                                (error) -> LoggerHelper.e("PINCODE", error.getMessage()),
                                () -> getViewState().clearError()
                        );
                return true;
            }
        }

        return false;
    }

    private boolean validate(CharSequence pincode) {
        if (pincode.length() != PinCodeView.PINCODE_LENGTH) {
            getViewState().showError(R.string.wrong_pincode_length);
            return false;
        }

        //---
        boolean isSame = true;
        char previousChar = pincode.charAt(0);
        for (int i = 1; i < pincode.length(); i++) {
            if (previousChar != pincode.charAt(i)) {
                isSame = false;
                break;
            }
        }

        if (isSame) {
            getViewState().showError(R.string.wrong_pincode_one_symbol);
            return false;
        }

        return true;
    }

    @Override
    public void onDestroy() {
        if (currentOperation != null) {
            currentOperation.dispose();
            currentOperation = null;
        }
        super.onDestroy();
    }

}
