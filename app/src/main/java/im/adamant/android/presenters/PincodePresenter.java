package im.adamant.android.presenters;

import com.arellomobile.mvp.InjectViewState;

import java.io.IOException;

import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.core.responses.Authorization;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.AuthorizeInteractor;
import im.adamant.android.interactors.KeypairInteractor;
import im.adamant.android.ui.mvp_view.PincodeView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class PincodePresenter extends BasePresenter<PincodeView> {
    private Router router;
    private KeypairInteractor keypairInteractor;
    private Mode currentMode = Mode.ENCRYPT_KEYPAIR;

    public enum Mode {
        ENCRYPT_KEYPAIR,
        RESTORE_KEYPAIR
    }

    public PincodePresenter(
            Router router,
            KeypairInteractor keypairInteractor,
            CompositeDisposable subscriptions
    ) {
        super(subscriptions);
        this.router = router;
        this.keypairInteractor = keypairInteractor;
    }

    public void setCurrentMode(Mode mode) {
        currentMode = mode;
    }

    public void onEnteredPincode(String pincode) {
        switch (currentMode) {
            case ENCRYPT_KEYPAIR: {
                encryptKeyPair(pincode);
            }
            break;
            case RESTORE_KEYPAIR: {
                restoreKeyPair(pincode);
            }
            break;
        }
    }

    private void encryptKeyPair(String pincode) {
        LoggerHelper.d("PIN-CREATED", pincode);

        Disposable subscribe = keypairInteractor
                .saveKeypair(pincode)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    getViewState().close();
                });
        subscriptions.add(subscribe);
    }

    private void restoreKeyPair(String pincode) {
        LoggerHelper.d("PIN-ENTERED", pincode);
        Disposable subscribe = keypairInteractor
                .restoreAuthorization(pincode)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(authorization -> {

                    if (authorization.isSuccess()){
                        router.navigateTo(Screens.CHATS_SCREEN);
                    } else {
                        router.navigateTo(Screens.LOGIN_SCREEN);
                    }
                })
                .doOnError(error -> {
                    if (error instanceof IOException){
                        getViewState().showMessage(R.string.authorization_error);
                    } else {
                        getViewState().showMessage(R.string.incorrect_pincode);
                    }

                })
                .retry((integer, throwable) -> throwable instanceof IOException)
                .onErrorReturn((throwable) -> {
                    Authorization authorization = new Authorization();
                    authorization.setSuccess(false);

                    return authorization;
                })
                .subscribe(authorization -> {
                    if (!authorization.isSuccess()){
                        router.navigateTo(Screens.LOGIN_SCREEN);
                    }
                });

        subscriptions.add(subscribe);
    }
}
