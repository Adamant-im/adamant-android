package im.adamant.android.interactors;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import im.adamant.android.BuildConfig;
import im.adamant.android.Constants;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.KeyStoreCipher;
import im.adamant.android.core.exceptions.WrongPincodeException;
import im.adamant.android.core.responses.Authorization;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.helpers.Settings;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class SecurityInteractor {
    private Settings settings;
    private AdamantApiWrapper api;
    private KeyStoreCipher keyStoreCipher;
    private Gson gson;

    public SecurityInteractor(Gson gson, Settings settings, AdamantApiWrapper api, KeyStoreCipher keyStoreCipher) {
        this.settings = settings;
        this.api = api;
        this.keyStoreCipher = keyStoreCipher;
        this.gson = gson;
    }

    // 1. Сохранение пасфразы и пинкода они не разрывны.
    public Completable savePassphrase(CharSequence pincode) {
        return Completable.fromAction(() -> {
            settings.setKeyPairMustBeStored(true);

            try {
                if (api.isAuthorized()){
                    KeyStoreCipher.SecureHash secureHash = keyStoreCipher.secureHash(pincode);
                    CharSequence passphrase = api.getPassPhrase();

                    String encryptedPassphrase = keyStoreCipher.encrypt(
                            Constants.ADAMANT_ACCOUNT_ALIAS,
                            passphrase
                    );

                    CombinedPassphrase combinedPassphrase = new CombinedPassphrase(encryptedPassphrase, secureHash);

                    String combined = keyStoreCipher.encrypt(
                            Constants.ADAMANT_ACCOUNT_ALIAS,
                            gson.toJson(combinedPassphrase)
                    );

                    settings.setAccountPassphrase(combined);
                }
            } catch (Exception e) {
                LoggerHelper.e("SaveKeyPair", e.getMessage(), e);
            }
        })
        .subscribeOn(Schedulers.computation())
        .timeout(BuildConfig.DEFAULT_OPERATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }


    public Completable dropPassphrase() {
        return Completable.fromAction(this::clearSettings);
    }

    public boolean isKeyPairMustBeStored() {
        return settings.isKeyPairMustBeStored();
    }

    // 2. Извлечение пассфразы из хранилища с проверкой пинкода и авторизация
    public Single<Authorization> restoreAuthorizationByPincode(CharSequence pincode) {
        return validatePincode(pincode)
                .flatMap((combinedPassphrase) -> {
                    CharSequence decryptedPassphrase = keyStoreCipher.decrypt(Constants.ADAMANT_ACCOUNT_ALIAS, combinedPassphrase.getEncryptedPassphrase());
                    return Single.just(decryptedPassphrase);
                })
                .subscribeOn(Schedulers.computation())
                .flatMap(passphrase -> api.authorize(passphrase).singleOrError())
                .timeout(BuildConfig.DEFAULT_OPERATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    // 3. Проверка пинкода
    public Single<CombinedPassphrase> validatePincode(CharSequence pincode) {
        return Single.defer(() -> {
            String encryptedCombinedPassphrase = settings.getAccountPassphrase();
            CharSequence decryptedPincode = keyStoreCipher.decrypt(Constants.ADAMANT_ACCOUNT_ALIAS, encryptedCombinedPassphrase);
            CombinedPassphrase combinedPassphrase = gson.fromJson(decryptedPincode.toString(), CombinedPassphrase.class);

            if (combinedPassphrase.getSecureHash() == null || combinedPassphrase.getSecureHash().getSalt() == null) {
                return Single.error(new WrongPincodeException("Not found saved salt."));
            }

            if (combinedPassphrase.getSecureHash().getHash() == null) {
                return Single.error(new WrongPincodeException("Not found saved hash."));
            }

            KeyStoreCipher.SecureHash enteredHash = keyStoreCipher.secureHash(pincode, combinedPassphrase.getSecureHash().getSalt());

            if (combinedPassphrase.getSecureHash().equals(enteredHash)) {
                return Single.just(combinedPassphrase);
            } else {
                return Single.error(new WrongPincodeException("Wrong pincode"));
            }
        });
    }

    public boolean isAuthorized() {
        return api.isAuthorized();
    }


    private void clearSettings() {
        settings.setKeyPairMustBeStored(false);
        settings.setAccountPassphrase("");
    }

    static class CombinedPassphrase {
        private String encryptedPassphrase;
        private KeyStoreCipher.SecureHash secureHash;

        public CombinedPassphrase(String encryptedPassphrase, KeyStoreCipher.SecureHash secureHash) {
            this.encryptedPassphrase = encryptedPassphrase;
            this.secureHash = secureHash;
        }

        public String getEncryptedPassphrase() {
            return encryptedPassphrase;
        }

        public KeyStoreCipher.SecureHash getSecureHash() {
            return secureHash;
        }
    }
}
