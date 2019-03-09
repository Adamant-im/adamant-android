package im.adamant.android.ui.presenters;

import com.arellomobile.mvp.InjectViewState;


import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.helpers.CharSequenceHelper;
import im.adamant.android.helpers.LoggerHelper;
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
        }
    }

    public void onClickLoginButton(CharSequence passPhrase) {
        passPhrase = CharSequenceHelper.trim(passPhrase);
        if (!authorizeInteractor.isValidPassphrase(passPhrase)){
            getViewState().loginError(R.string.wrong_passphrase);
            return;
        }

        getViewState().lockUI();

        final CharSequence finalPassPhrase = passPhrase;
        Disposable subscription = authorizeInteractor
                .authorize(passPhrase)
                .subscribe(
                    (authorize)->{
                        if (authorize.isSuccess()){
                            router.navigateTo(Screens.WALLET_SCREEN);
                        } else {

                            //TODO: Oh my god, somebody fix this, please.
                            //PWA adamantServerApi.js, line: 129
                            if ("Account not found".equalsIgnoreCase(authorize.getError())) {
                                createNewAccount(finalPassPhrase);
                            } else {
                                LoggerHelper.e("ERR", authorize.getError());
                                router.showSystemMessage(authorize.getError());
                            }
                        }
                    },
                    (error) -> {
                        LoggerHelper.e("ERR", error.getMessage(), error);
                        router.showSystemMessage(error.getMessage());
                        getViewState().unlockUI();
                    }
                );

       subscriptions.add(subscription);
    }


    public void onInputPassphrase(CharSequence passphrase){
        passphrase = CharSequenceHelper.trim(passphrase);
        if (authorizeInteractor.isValidPassphrase(passphrase)) {
            getViewState().unlockUI();
        } else {
            getViewState().lockUI();
        }
    }

    public void onClickScanQrCodeButton() {
        router.navigateTo(Screens.SCAN_QRCODE_SCREEN);
    }

    private void createNewAccount(CharSequence passphrase) {
        Disposable subscription = authorizeInteractor
                .createNewAccount(passphrase)
                .subscribe(
                        (authorize)->{
                            if (authorize.isSuccess()){
                                router.navigateTo(Screens.CHATS_SCREEN);
                            } else {
                                LoggerHelper.e("ERR", authorize.getError());
                                router.showSystemMessage(authorize.getError());
                            }
                        },
                        (error) -> {
                            router.showSystemMessage(error.getMessage());
                        },
                        () -> getViewState().unlockUI()
                );

        subscriptions.add(subscription);
    }
}
