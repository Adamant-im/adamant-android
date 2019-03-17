package im.adamant.android.ui.presenters;

import com.arellomobile.mvp.InjectViewState;

import im.adamant.android.BuildConfig;
import im.adamant.android.R;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.SecurityInteractor;
import im.adamant.android.ui.mvp_view.PinCodeView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class PincodePresenter extends BasePresenter<PinCodeView> {
    private SecurityInteractor pinCodeInteractor;
    private PinCodeView.MODE mode = PinCodeView.MODE.ACCESS_TO_APP;
    private String pincodeForConfirmation;
    private boolean ignoreInput = false;
    private int attemptsCount = 0;
    private long lastAttemptTimestamp = 0;
    private Disposable currentOperation;

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
        if (ignoreInput) { return; }

        if (!validate(pinCode)) { return; }

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
                    startProcess();
                    currentOperation = pinCodeInteractor
                            .savePassphrase(pinCode)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    () -> {
                                        stopProcess();
                                        getViewState().close();
                                    },
                                    error -> {
                                        stopProcess();
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

                if (attemptsCount > BuildConfig.MAX_WRONG_PINCODE_ATTEMTS) {
                    if (lastAttemptTimestamp < (System.currentTimeMillis() - BuildConfig.WRONG_PINCODE_WAIT_MILISECONDS)) {
                        attemptsCount = 0;
                    } else {
                        getViewState().showError(R.string.pincode_exceeding_the_number_of_attempts);
                        getViewState().shuffleKeyboard();
                        return;
                    }
                }

                lastAttemptTimestamp = System.currentTimeMillis();

                startProcess();
                currentOperation = pinCodeInteractor
                        .restoreAuthorizationByPincode(pinCode)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                (authorization) -> {
                                    stopProcess();
                                    if (authorization.isSuccess()) {
                                        getViewState().goToMain();
                                    } else {
                                        getViewState().showError(R.string.account_not_found);
                                    }
                                },
                                error -> {
                                    stopProcess();
                                    getViewState().showError(R.string.wrong_pincode);
                                    LoggerHelper.e("PINCODE", error.getMessage(), error);
                                }
                        );
            }
            break;
            case DROP: {
                startProcess();
                currentOperation = pinCodeInteractor
                        .dropPassphrase(pinCode)
                        .doOnError((throwable -> {LoggerHelper.e("PINCODE", "after ignore UNSUCESS");}))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                (value) -> {
                                    LoggerHelper.e("PINCODE", "SUCCESS SUBSCRIBE");
                                    stopProcess();
                                    getViewState().close();
                                },
                                error -> {
                                    stopProcess();
                                    getViewState().showError(R.string.wrong_pincode);
                                    LoggerHelper.e("PINCODE", error.getMessage(), error);
                                }
                        );
            }
            break;
        }
    }

    private void startProcess() {
        ignoreInput = true;
        getViewState().startProcess();
    }

    private void stopProcess() {
        ignoreInput = false;
        getViewState().stopProcess();
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
