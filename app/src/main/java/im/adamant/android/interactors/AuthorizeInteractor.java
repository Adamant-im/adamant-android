package im.adamant.android.interactors;

import com.goterl.lazycode.lazysodium.utils.KeyPair;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import im.adamant.android.BuildConfig;
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

    public Flowable<Authorization> authorize(CharSequence passPhrase) {
        try {
            return api.authorize(passPhrase)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(authorization -> {
                        if (settings.isKeyPairMustBeStored()){
                            String account = keyStoreCipher.encrypt(
                                    Constants.ADAMANT_ACCOUNT_ALIAS,
                                    api.getPassPhrase()
                            );
                            settings.setAccountPassphrase(account);
                        }
                    })
                    .timeout(BuildConfig.DEFAULT_OPERATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }catch (Exception ex){
            ex.printStackTrace();
            return Flowable.error(ex);
        }
    }


    public CharSequence generatePassPhrase() {
        return keyGenerator.generateNewPassphrase();
    }

    public boolean isValidPassphrase(CharSequence passphrase) {
        return keyGenerator.isValidPassphrase(passphrase);
    }

    public void validatePassphrase(String passphrase) throws WordNotFoundException, UnexpectedWhiteSpaceException, InvalidWordCountException, InvalidChecksumException {
            keyGenerator.validatePassphrase(passphrase);
    }

    public Flowable<Authorization> createNewAccount(CharSequence passPhrase) {
        return api.createNewAccount(passPhrase)
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(BuildConfig.DEFAULT_OPERATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
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
