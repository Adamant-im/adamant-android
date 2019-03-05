package im.adamant.android.interactors;

import im.adamant.android.Constants;
import im.adamant.android.core.encryption.KeyStoreCipher;
import im.adamant.android.helpers.Settings;
import io.reactivex.Completable;
import io.reactivex.Flowable;

public class PincodeInteractor {
    private KeyStoreCipher keyStoreCipher;
    private Settings settings;

    public PincodeInteractor(KeyStoreCipher keyStoreCipher, Settings settings) {
        this.keyStoreCipher = keyStoreCipher;
        this.settings = settings;
    }

    public Completable createPincode(String pinCode) {
        return Completable.complete();
    }

    public Flowable<Boolean> verifyPincode(String pinCode) {
        return Flowable.empty();
    }
}
