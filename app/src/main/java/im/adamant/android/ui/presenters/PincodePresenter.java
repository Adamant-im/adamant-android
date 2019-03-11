package im.adamant.android.ui.presenters;

import android.os.Bundle;

import com.arellomobile.mvp.InjectViewState;

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
    private PinCodeView.MODE mode = PinCodeView.MODE.VERIFY;

    public PincodePresenter(SecurityInteractor pinCodeInteractor, CompositeDisposable subscriptions) {
        super(subscriptions);
        this.pinCodeInteractor = pinCodeInteractor;
    }

    public PincodePresenter(CompositeDisposable subscriptions) {
        super(subscriptions);
    }

    public void setMode(PinCodeView.MODE mode) {
        this.mode = mode;
        switch (mode){
            case CREATE: {
                getViewState().setSuggestion(R.string.activity_pincode_enter_new_pincode);
            }
            break;
            case VERIFY: {
                getViewState().setSuggestion(R.string.activity_pincode_enter_pincode);
            }
            break;
        }
    }

    public void onInputPincodeWasCompleted(String pinCode) {
        switch (mode){
            case CREATE: {
                Disposable subscription = pinCodeInteractor
                        .savePassphrase(pinCode)
                        .subscribeOn(Schedulers.computation())
                        .subscribe(
                                () -> {
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean(PinCodeView.ARG_CREATED, true);
                                    getViewState().close();
                                },
                                error -> LoggerHelper.e("PINCODE", error.getMessage(), error)
                        );
                subscriptions.add(subscription);
            }
            break;
            case VERIFY: {
                Disposable subscription = pinCodeInteractor
                        .restoreAuthorizationByPincode(pinCode)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                (authorization) -> {
                                    if (authorization.isSuccess()) {
                                        Bundle bundle = new Bundle();
                                        bundle.putBoolean(PinCodeView.ARG_VERIFIED, true);
                                        getViewState().goToMain();
                                    } else {
                                        //TODO: Обработка ошибок
                                        getViewState().showError(R.string.wrong_pincode);
                                    }
                                },
                                error -> LoggerHelper.e("PINCODE", error.getMessage(), error)
                        );
                subscriptions.add(subscription);
            }
            break;
        }
    }
}
