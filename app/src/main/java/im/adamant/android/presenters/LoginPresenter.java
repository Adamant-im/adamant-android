package im.adamant.android.presenters;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;

import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.image.ImageType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.core.responses.Authorization;
import im.adamant.android.helpers.QrCodeHelper;
import im.adamant.android.interactors.AuthorizeInteractor;
import im.adamant.android.ui.mvp_view.LoginView;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class LoginPresenter extends BasePresenter<LoginView> {
    private Router router;
    private AuthorizeInteractor authorizeInteractor;

    public LoginPresenter(
            Router router,
            AuthorizeInteractor authorizeInteractor,
            CompositeDisposable subscriptions
    ) {
        super(subscriptions);
        this.router = router;
        this.authorizeInteractor = authorizeInteractor;
    }

    @Override
    public void attachView(LoginView view) {
        super.attachView(view);

        if (authorizeInteractor.isAuthorized()){
            router.navigateTo(Screens.WALLET_SCREEN);
        } else {
            Disposable subscribe = authorizeInteractor
                    .restoreAuthorization()
                    .subscribe(
                        authorization -> {
                            if (authorization.isSuccess()){
                                router.navigateTo(Screens.WALLET_SCREEN);
                            }
                        },
                        Throwable::printStackTrace
                    );


            //TODO: If "Not Authorized" exception than force logout from application.

            subscriptions.add(subscribe);
        }
    }

    public void onClickLoginButton(String passPhrase) {
        passPhrase = passPhrase.trim();
        if (!authorizeInteractor.isValidPassphrase(passPhrase)){
            getViewState().loginError(R.string.wrong_passphrase);
            return;
        }

        getViewState().lockAuthorization();

        String finalPassPhrase = passPhrase;
        Disposable subscription = authorizeInteractor.authorize(passPhrase).subscribe(
                (authorize)->{
                    if (authorize.isSuccess()){
                        router.navigateTo(Screens.WALLET_SCREEN);
                    } else {

                        //TODO: Oh my god, somebody fix this, please.
                        //PWA adamantServerApi.js, line: 129
                        if ("Account not found".equalsIgnoreCase(authorize.getError())) {
                            onClickCreateNewAccount(finalPassPhrase);
                        } else {
                            Log.e("ERR", authorize.getError());
                            router.showSystemMessage(authorize.getError());
                        }
                    }
                },
                (error) -> {
                    error.printStackTrace();
                    router.showSystemMessage(error.getMessage());
                    getViewState().unLockAuthorization();
                },
                () -> getViewState().unLockAuthorization()
        );

       subscriptions.add(subscription);
    }

    public void onClickGeneratePassphrase() {
        getViewState().passPhraseWasGenerated(
                authorizeInteractor.generatePassPhrase()
        );
    }

    public void onClickCreateNewAccount(String passPhrase) {
        passPhrase = passPhrase.trim();

        if (!authorizeInteractor.isValidPassphrase(passPhrase)){
            getViewState().loginError(R.string.wrong_passphrase);
        }

        getViewState().lockAuthorization();

        Disposable subscription = authorizeInteractor
                .createNewAccount(passPhrase)
                .subscribe(
                        (authorize)->{
                            if (authorize.isSuccess()){
                                router.navigateTo(Screens.WALLET_SCREEN);
                            } else {
                                Log.e("ERR", authorize.getError());
                                router.showSystemMessage(authorize.getError());
                            }
                        },
                        (error) -> {
                            router.showSystemMessage(error.getMessage());
                        },
                        () -> getViewState().unLockAuthorization()
                );

        subscriptions.add(subscription);
    }

    public void onClickScanQrCodeButton() {
        router.navigateTo(Screens.SCAN_QRCODE_SCREEN);
    }

}
