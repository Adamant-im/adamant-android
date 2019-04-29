package im.adamant.android.interactors;

import android.os.Build;
import android.security.keystore.KeyInfo;

import com.google.gson.Gson;

import java.security.InvalidKeyException;
import java.util.concurrent.TimeUnit;

import im.adamant.android.BuildConfig;
import im.adamant.android.Constants;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.KeyStoreCipher;
import im.adamant.android.core.exceptions.EncryptionException;
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
    private SwitchPushNotificationServiceInteractor pushNotificationServiceInteractor;
    private Gson gson;

    public SecurityInteractor(
            Gson gson,
            Settings settings,
            AdamantApiWrapper api,
            KeyStoreCipher keyStoreCipher,
            SwitchPushNotificationServiceInteractor pushNotificationServiceInteractor
    ) {
        this.settings = settings;
        this.api = api;
        this.keyStoreCipher = keyStoreCipher;
        this.gson = gson;
        this.pushNotificationServiceInteractor = pushNotificationServiceInteractor;
    }

    // 1. Сохранение пасфразы и пинкода они не разрывны.
    public Completable savePassphrase(CharSequence pincode) {
        return Completable.fromAction(() -> {
            settings.setKeyPairMustBeStored(true);

            try {
                if (api.isAuthorized()){
                    String secureHash = keyStoreCipher.secureHash(pincode);
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

    public Single<CombinedPassphrase> dropPassphrase(CharSequence pincode) {
        return validatePincode(pincode)
                .flatMap(combinedPassphrase -> pushNotificationServiceInteractor
                        .resetNotificationFacade(true)
                        .toSingleDefault(combinedPassphrase)
                )
                .doAfterSuccess((combinedPassphrase) -> clearSettings())
                .subscribeOn(Schedulers.computation())
                .timeout(BuildConfig.DEFAULT_OPERATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
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
                .doOnError((throwable) -> {
                    boolean isCriticalError = (throwable instanceof EncryptionException);
                    if (isCriticalError) {
                        clearSettings();
                    }
                })
                .subscribeOn(Schedulers.computation())
                .flatMap(passphrase -> api.authorize(passphrase).singleOrError())
                .timeout(BuildConfig.DEFAULT_OPERATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    public Single<Authorization> restoreAuthorizationWithoutPincode() {
        return getCombinedPassphrase()
                .flatMap((combinedPassphrase) -> {
                    CharSequence decryptedPassphrase = keyStoreCipher.decrypt(Constants.ADAMANT_ACCOUNT_ALIAS, combinedPassphrase.getEncryptedPassphrase());
                    return Single.just(decryptedPassphrase);
                })
                .doOnError((throwable) -> {
                    boolean isCriticalError = (throwable instanceof EncryptionException);
                    if (isCriticalError) {
                        clearSettings();
                    }
                })
                .subscribeOn(Schedulers.computation())
                .flatMap(passphrase -> api.authorize(passphrase).singleOrError())
                .timeout(BuildConfig.DEFAULT_OPERATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    // 3. Проверка пинкода
    private Single<CombinedPassphrase> validatePincode(CharSequence pincode) {
        String localPincode = pincode.toString(); //Copying pincode to protect against modification
        return getCombinedPassphrase()
                .map((combinedPassphrase) -> {
                    if (keyStoreCipher.verifyHash(combinedPassphrase.getSecureHash(), localPincode)) {
                        return combinedPassphrase;
                    } else {
                        throw new WrongPincodeException("Wrong pincode");
                    }
                });
    }

    public boolean isAuthorized() {
        return api.isAuthorized();
    }

    public boolean isHardwareSecuredDevice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            KeyInfo keyInfo = keyStoreCipher.provideKeyInfo(Constants.ADAMANT_ACCOUNT_ALIAS);
            if (keyInfo == null) { return false; }

            return keyInfo.isInsideSecureHardware();
        }

        return false;
    }


    private void clearSettings() {
        settings.setKeyPairMustBeStored(false);
        settings.setAccountPassphrase("");
    }

    private Single<CombinedPassphrase> getCombinedPassphrase() {
        return Single.fromCallable(() -> {
            String encryptedCombinedPassphrase = settings.getAccountPassphrase();
            CharSequence decryptedPincode = keyStoreCipher.decrypt(Constants.ADAMANT_ACCOUNT_ALIAS, encryptedCombinedPassphrase);
            CombinedPassphrase combinedPassphrase = gson.fromJson(decryptedPincode.toString(), CombinedPassphrase.class);

            if (combinedPassphrase.getSecureHash() == null) {
                throw new WrongPincodeException("Not found saved hash.");
            }

            return combinedPassphrase;
        });
    }

    public static class CombinedPassphrase {
        private String encryptedPassphrase;
        private String secureHash;

        public CombinedPassphrase(String encryptedPassphrase, String secureHash) {
            this.encryptedPassphrase = encryptedPassphrase;
            this.secureHash = secureHash;
        }

        public String getEncryptedPassphrase() {
            return encryptedPassphrase;
        }

        public String getSecureHash() {
            return secureHash;
        }
    }
}
