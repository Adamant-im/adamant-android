package im.adamant.android.ui.presenters;

import android.os.Bundle;

import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import im.adamant.android.R;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.PincodeInteractor;
import im.adamant.android.ui.mvp_view.PinCodeView;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class PincodePresenter extends BasePresenter<PinCodeView> {
    private PincodeInteractor pinCodeInteractor;
    private PinCodeView.MODE mode = PinCodeView.MODE.VERIFY;

    public PincodePresenter(PincodeInteractor pinCodeInteractor, CompositeDisposable subscriptions) {
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
        PinCodeView viewState = getViewState();
        switch (mode){
            case CREATE: {
                Disposable subscription = pinCodeInteractor
                        .createPincode(pinCode)
                        .subscribeOn(Schedulers.computation())
                        .subscribe(
                                () -> {
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean(PinCodeView.ARG_CREATED, true);
                                    viewState.close(bundle);
                                },
                                error -> LoggerHelper.e("PINCODE", error.getMessage(), error)
                        );
                subscriptions.add(subscription);
            }
            break;
            case VERIFY: {
                Disposable subscription = pinCodeInteractor
                        .verifyPincode(pinCode)
                        .subscribeOn(Schedulers.computation())
                        .subscribe(
                                (verified) -> {
                                    if (verified) {
                                        Bundle bundle = new Bundle();
                                        bundle.putBoolean(PinCodeView.ARG_VERIFIED, true);
                                        viewState.close(bundle);
                                    } else {
                                        //TODO: Обработка ошибок
                                        viewState.showError(R.string.wrong_pincode);
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
