package im.adamant.android.ui.presenters;

import com.arellomobile.mvp.InjectViewState;


import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.helpers.CharSequenceHelper;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.AuthorizeInteractor;
import im.adamant.android.rx.RxTaskManager;
import im.adamant.android.ui.mvp_view.LoginView;

import io.github.novacrypto.bip39.Validation.InvalidChecksumException;
import io.github.novacrypto.bip39.Validation.InvalidWordCountException;
import io.github.novacrypto.bip39.Validation.UnexpectedWhiteSpaceException;
import io.github.novacrypto.bip39.Validation.WordNotFoundException;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class LoginPresenter extends BasePresenter<LoginView> {
    private Router router;
    private AuthorizeInteractor authorizeInteractor;

    public LoginPresenter(
            Router router,
            AuthorizeInteractor authorizeInteractor
    ) {
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
        if (validate(passPhrase)) {

            getViewState().lockUI();

            final CharSequence finalPassPhrase = passPhrase;
            Disposable subscription = authorizeInteractor
                    .authorize(passPhrase)
                    .subscribe(
                            (authorize)->{
                                if (authorize.isSuccess()){
                                    router.navigateTo(Screens.WALLET_SCREEN);
                                    getViewState().unlockUI();
                                } else {

                                    //TODO: Oh my god, somebody fix this, please.
                                    //PWA adamantServerApi.js, line: 129
                                    if ("Account not found".equalsIgnoreCase(authorize.getError())) {
                                        createNewAccount(finalPassPhrase);
                                    } else {
                                        LoggerHelper.e("ERR", authorize.getError());
                                        getViewState().networkError(authorize.getError());
                                        getViewState().unlockUI();
                                    }
                                }
                            },
                            (error) -> {
                                LoggerHelper.e("ERR", error.getMessage(), error);
                                getViewState().networkError(error.getMessage());
                                getViewState().unlockUI();
                            }
                    );

            subscriptions.add(subscription);
        }
    }


//    public void onInputPassphrase(CharSequence passphrase){
//        passphrase = CharSequenceHelper.trim(passphrase);
//        if (authorizeInteractor.isValidPassphrase(passphrase)) {
//            getViewState().unlockUI();
//        } else {
//            getViewState().lockUI();
//        }
//    }

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
                                getViewState().networkError(authorize.getError());
                            }

                            getViewState().unlockUI();
                        },
                        (error) -> {
                            getViewState().networkError(error.getMessage());
                            getViewState().unlockUI();
                        }
                );

        subscriptions.add(subscription);
    }

    private boolean validate(CharSequence passphrase) {
        String passPhrase = passphrase.toString();
        String[] words = passPhrase.split(" ");
        int current = words.length;
        int necessary = 12 - current;

        try {
            authorizeInteractor.validatePassphrase(passPhrase);

            //The InvalidWordCountException exception is not always called.
            //(For example: in some cases, the passphrase of 9 words does not raise an exception), therefore this check is implemented
            if (current < 12) {
                getViewState().invalidCount(current, necessary);
                return false;
            }

            return true;
        } catch (WordNotFoundException e) {
            getViewState().invalidWords(e.getWord(), e.getSuggestion1(), e.getSuggestion2());
            return false;
        } catch (UnexpectedWhiteSpaceException e) {
            getViewState().invalidSymbol();
            return false;
        } catch (InvalidWordCountException e) {
            getViewState().invalidCount(current, necessary);
            return false;
        } catch (InvalidChecksumException e) {
            getViewState().invalidChecksum();
            return false;
        }
    }
}
