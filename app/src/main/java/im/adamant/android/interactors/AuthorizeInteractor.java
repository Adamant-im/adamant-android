package im.adamant.android.interactors;

import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.encryption.KeyGenerator;
import im.adamant.android.core.helpers.interfaces.AuthorizationStorage;
import im.adamant.android.core.requests.NewAccount;
import im.adamant.android.core.responses.Authorization;
import com.goterl.lazycode.lazysodium.utils.KeyPair;

import io.github.novacrypto.bip39.MnemonicValidator;
import io.github.novacrypto.bip39.Validation.InvalidChecksumException;
import io.github.novacrypto.bip39.Validation.InvalidWordCountException;
import io.github.novacrypto.bip39.Validation.UnexpectedWhiteSpaceException;
import io.github.novacrypto.bip39.Validation.WordNotFoundException;
import io.github.novacrypto.bip39.wordlists.English;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class AuthorizeInteractor {
    private AdamantApi api;
    private AuthorizationStorage storage;
    private KeyGenerator keyGenerator;

    public AuthorizeInteractor(
            AdamantApi api,
            AuthorizationStorage storage,
            KeyGenerator keyGenerator
    ) {
        this.api = api;
        this.storage = storage;
        this.keyGenerator = keyGenerator;
    }

    public Flowable<Authorization> authorize(String passPhrase){
        try {
            KeyPair keyPair = keyGenerator.getKeyPairFromPassPhrase(passPhrase);
            return api.authorize(keyPair.getPublicKeyString().toLowerCase())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext((authorization -> {
                        setAuthorization(authorization, keyPair);
                    }));
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
        KeyPair keyPair = keyGenerator.getKeyPairFromPassPhrase(passPhrase);

        NewAccount newAccount = new NewAccount();
        newAccount.setPublicKey(keyPair.getPublicKeyString().toLowerCase());

        return api.createNewAccount(newAccount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext((authorization -> {
                    setAuthorization(authorization, keyPair);
                }));
    }

    private void setAuthorization(Authorization authorization, KeyPair keyPair){
        if (authorization.isSuccess()){
            storage.setAuth(authorization.getAccount(), keyPair);
        }
    }
}
