package im.adamant.android.interactors;

import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.KeyGenerator;
import im.adamant.android.core.responses.Authorization;

import io.github.novacrypto.bip39.MnemonicValidator;
import io.github.novacrypto.bip39.Validation.InvalidChecksumException;
import io.github.novacrypto.bip39.Validation.InvalidWordCountException;
import io.github.novacrypto.bip39.Validation.UnexpectedWhiteSpaceException;
import io.github.novacrypto.bip39.Validation.WordNotFoundException;
import io.github.novacrypto.bip39.wordlists.English;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;


public class AuthorizeInteractor {
    private AdamantApiWrapper api;
    private KeyGenerator keyGenerator;

    public AuthorizeInteractor(
            AdamantApiWrapper api,
            KeyGenerator keyGenerator
    ) {
        this.api = api;
        this.keyGenerator = keyGenerator;
    }

    public Flowable<Authorization> authorize(String passPhrase){
        try {
            return api.authorize(passPhrase)
                    .observeOn(AndroidSchedulers.mainThread());
        }catch (Exception ex){
            ex.printStackTrace();
            return Flowable.error(ex);
        }

    }

    public CharSequence generatePassPhrase() {
        return keyGenerator.generateNewPassphrase();
    }

    public boolean isValidPassphrase(String passphrase) {
        try {
            MnemonicValidator
                    .ofWordList(English.INSTANCE)
                    .validate(passphrase);
        } catch (InvalidChecksumException | InvalidWordCountException | WordNotFoundException | UnexpectedWhiteSpaceException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public Flowable<Authorization> createNewAccount(String passPhrase) {
        return api.createNewAccount(passPhrase)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public boolean isAuthorized() {
        return api.isAuthorized();
    }
}
