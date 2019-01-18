package im.adamant.android.ui.presenters;


import android.util.Pair;

import com.arellomobile.mvp.InjectViewState;

import java.util.ArrayList;
import java.util.List;

import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.AuthorizeInteractor;
import im.adamant.android.ui.mvp_view.RegistrationView;
import io.github.novacrypto.bip39.Validation.InvalidChecksumException;
import io.github.novacrypto.bip39.Validation.InvalidWordCountException;
import io.github.novacrypto.bip39.Validation.UnexpectedWhiteSpaceException;
import io.github.novacrypto.bip39.Validation.WordNotFoundException;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class RegistrationPresenter extends BasePresenter<RegistrationView> {
    private Router router;
    private AuthorizeInteractor authorizeInteractor;

    private List<Pair<String, String>> passphrases = new ArrayList<>();
    private int currentItemIndex = 0;

    public RegistrationPresenter(Router router, AuthorizeInteractor authorizeInteractor, CompositeDisposable subscriptions) {
        super(subscriptions);
        this.authorizeInteractor = authorizeInteractor;
        this.router = router;

//        passphrases.add(currentItemIndex, new Pair<>("", ""));

        onClickGeneratePassphrases();
    }

    @Override
    public void attachView(RegistrationView view) {
        super.attachView(view);
        getViewState().updatePassphraseList(passphrases);
    }

    public void onInputPassphrase(String passphrase) {
        passphrase = passphrase.trim();
        String pkey = "";

        if (validate(passphrase)){
            pkey = authorizeInteractor.getPublicKeyFromPassphrase(passphrase);
        }

        passphrases.remove(currentItemIndex);
        passphrases.add(currentItemIndex, new Pair<>(passphrase, pkey));

        getViewState().updatePassphraseList(passphrases);
    }


    public void onClickGeneratePassphrases() {
        passphrases.clear();
        for (int i = 0; i < 3; i++) {
            String passPhrase = authorizeInteractor.generatePassPhrase().toString();
            String pkey = authorizeInteractor.getPublicKeyFromPassphrase(passPhrase);

            passphrases.add(new Pair<>(passPhrase, pkey));
        }

        getViewState().updatePassphraseList(passphrases);

        Pair<String, String> pair = passphrases.get(currentItemIndex);
        getViewState().showPassphrase(pair.first);
    }

    public void onSelectedPassphrase(int index) {
        if (currentItemIndex != index) {
            currentItemIndex = index;
            Pair<String, String> pair = passphrases.get(currentItemIndex);
            getViewState().showPassphrase(pair.first);
        }
    }

    public void onClickRegisterAccount() {
        Pair<String, String> pair = passphrases.get(currentItemIndex);
        String passphrase = pair.first.trim();

        if (validate(passphrase)){
            getViewState().lockUI();

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

    private boolean validate(String passphrase) {
        String[] words = passphrase.split(" ");
        int current = words.length;
        int necessary = 12 - current;

        try {
            authorizeInteractor.validatePassphrase(passphrase);

            //The InvalidWordCountException exception is not always called.
            //(For example: in some cases, the passphrase of 9 words does not raise an exception), therefore this check is implemented
            if (current < 12) {
                getViewState().invalidCount(current, necessary);
                return false;
            }

            getViewState().onEnteredValidPassphrase();
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

        return true;
    }
}
