package im.adamant.android.interactors;

import java.util.concurrent.TimeUnit;

import im.adamant.android.BuildConfig;
import im.adamant.android.Constants;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.KeyStoreCipher;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.helpers.Settings;
import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SaveKeypairInteractor {
    private Settings settings;
    private AdamantApiWrapper api;
    private KeyStoreCipher keyStoreCipher;

    private Disposable subscription;

    public SaveKeypairInteractor(
            Settings settings,
            AdamantApiWrapper api,
            KeyStoreCipher keyStoreCipher
    ) {
        this.settings = settings;
        this.api = api;
        this.keyStoreCipher = keyStoreCipher;
    }

    public Completable saveKeypair(boolean value) {
        return Completable.fromAction(() -> {
                    settings.setKeyPairMustBeStored(value);

                    if (value){
                        try {
                            if (api.isAuthorized()){
                                String account = keyStoreCipher.encrypt(
                                        Constants.ADAMANT_ACCOUNT_ALIAS,
                                        api.getPassPhrase()
                                );
                                settings.setAccountPassphrase(account);
                            }
                        } catch (Exception e) {
                            LoggerHelper.e("SaveKeyPair", e.getMessage(), e);
                        }
                    } else {
                        settings.setAccountPassphrase("");
                    }
                })
                .subscribeOn(Schedulers.computation())
                .timeout(BuildConfig.DEFAULT_OPERATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    public boolean isKeyPairMustBeStored() {
        return settings.isKeyPairMustBeStored();
    }


    @Override
    protected void finalize() throws Throwable {
        if (subscription != null) {
            subscription.dispose();
        }

        super.finalize();
    }
}
