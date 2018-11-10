package im.adamant.android.presenters;


import android.util.Pair;

import com.arellomobile.mvp.InjectViewState;

import java.util.ArrayList;
import java.util.List;

import im.adamant.android.interactors.AuthorizeInteractor;
import im.adamant.android.ui.mvp_view.RegistrationView;
import io.github.novacrypto.bip39.Validation.InvalidChecksumException;
import io.github.novacrypto.bip39.Validation.InvalidWordCountException;
import io.github.novacrypto.bip39.Validation.UnexpectedWhiteSpaceException;
import io.github.novacrypto.bip39.Validation.WordNotFoundException;
import io.reactivex.disposables.CompositeDisposable;

@InjectViewState
public class RegistrationPresenter extends BasePresenter<RegistrationView> {
    private AuthorizeInteractor authorizeInteractor;

    private List<Pair<String, String>> passphrases = new ArrayList<>();
    private int currentItemIndex = 0;

    public RegistrationPresenter(AuthorizeInteractor authorizeInteractor, CompositeDisposable subscriptions) {
        super(subscriptions);
        this.authorizeInteractor = authorizeInteractor;
        passphrases.add(currentItemIndex, new Pair<>("", ""));
    }

    @Override
    public void attachView(RegistrationView view) {
        super.attachView(view);
        getViewState().updatePassphraseList(passphrases);
    }

    public void onInputPassphrase(String passphrase) {
        passphrase = passphrase.trim();
        String pkey = "";

        String[] words = passphrase.split(" ");
        int current = words.length;
        int necessary = 12 - current;

        if (current < 12) {
            getViewState().invalidCount(current, necessary);
            return;
        }

        try {
            authorizeInteractor.validatePassphrase(passphrase);
            pkey = authorizeInteractor.getPublicKeyFromPassphrase(passphrase);

            getViewState().onEnteredValidPassphrase();
        } catch (WordNotFoundException e) {
            getViewState().invalidWords(e.getWord(), e.getSuggestion1(), e.getSuggestion2());
        } catch (UnexpectedWhiteSpaceException e) {
            getViewState().invalidSymbol();
        } catch (InvalidWordCountException e) {
            getViewState().invalidCount(current, necessary);
        } catch (InvalidChecksumException e) {
            getViewState().invalidChecksum();
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
}
