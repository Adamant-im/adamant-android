package im.adamant.android.interactors;

import com.goterl.lazycode.lazysodium.utils.KeyPair;

import java.util.concurrent.Callable;

import im.adamant.android.Constants;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.AdamantKeyGenerator;
import im.adamant.android.core.encryption.KeyStoreCipher;
import im.adamant.android.core.responses.Authorization;

import im.adamant.android.helpers.Settings;
import io.github.novacrypto.bip39.Validation.InvalidChecksumException;
import io.github.novacrypto.bip39.Validation.InvalidWordCountException;
import io.github.novacrypto.bip39.Validation.UnexpectedWhiteSpaceException;
import io.github.novacrypto.bip39.Validation.WordNotFoundException;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class AuthorizeInteractor {

    private AdamantApiWrapper api;
    private AdamantKeyGenerator keyGenerator;
    private KeyStoreCipher keyStoreCipher;
    private Settings settings;

    public AuthorizeInteractor(
            AdamantApiWrapper api,
            AdamantKeyGenerator keyGenerator,
            KeyStoreCipher keyStoreCipher,
            Settings settings
    ) {
        this.api = api;
        this.keyGenerator = keyGenerator;
        this.keyStoreCipher = keyStoreCipher;
        this.settings = settings;
    }

    public Flowable<Authorization> authorize(String passPhrase) {
        try {
            return api.authorize(passPhrase)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(authorization -> {
                        if (settings.isKeyPairMustBeStored()){
                            String account = keyStoreCipher.encrypt(Constants.ADAMANT_ACCOUNT_ALIAS, api.getAdamantKeyPair());
                            settings.setAccountKeypair(account);
                        }
                    });
        }catch (Exception ex){
            ex.printStackTrace();
            return Flowable.error(ex);
        }
    }

    public Flowable<Authorization> restoreAuthorization() {

            //Not transform this code in lambda (Application crashed if unchecked exception)
           return Flowable.fromCallable(new Callable<KeyPair>() {
                    @Override
                    public KeyPair call() throws Exception {
                        String account = settings.getAccountKeypair();
                        if (account == null || account.isEmpty()){
                            throw new Exception("Account not stored!");
                        }

                        KeyPair keyPair = keyStoreCipher.decrypt(Constants.ADAMANT_ACCOUNT_ALIAS, account);

                        if (keyPair == null) {
                            throw new Exception("Account not decrypted!");
                        }

                        return keyPair;
                    }
                })
               .subscribeOn(Schedulers.computation())
               .flatMap(keyPair -> api.authorize(keyPair));

    }

    public CharSequence generatePassPhrase() {
        return keyGenerator.generateNewPassphrase();
    }

    public boolean isValidPassphrase(String passphrase) {
        return keyGenerator.isValidPassphrase(passphrase);
    }

    public void validatePassphrase(String passphrase) throws WordNotFoundException, UnexpectedWhiteSpaceException, InvalidWordCountException, InvalidChecksumException {
            keyGenerator.validatePassphrase(passphrase);
    }

    public Flowable<Authorization> createNewAccount(String passPhrase) {
        return api.createNewAccount(passPhrase)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public String getPublicKeyFromPassphrase(String passphrase) {
        if (keyGenerator.isValidPassphrase(passphrase)){
            KeyPair keyPair = keyGenerator.getKeyPairFromPassPhrase(passphrase);
            return keyPair.getPublicKeyString().toLowerCase();
        } else {
            return "";
        }
    }

    public boolean isAuthorized() {
        return api.isAuthorized();
    }
}
