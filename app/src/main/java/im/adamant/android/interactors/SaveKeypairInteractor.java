package im.adamant.android.interactors;

import im.adamant.android.Constants;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.KeyStoreCipher;
import im.adamant.android.helpers.Settings;
import im.adamant.android.rx.ObservableRxList;
import im.adamant.android.core.entities.ServerNode;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class SaveKeypairInteractor {
    private Settings settings;
    private AdamantApiWrapper api;
    private KeyStoreCipher keyStoreCipher;

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
                                api.getKeyPair()
                        );
                        settings.setAccountKeypair(account);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                settings.setAccountKeypair("");
            }
        }).subscribeOn(Schedulers.computation());
    }

    public boolean isKeyPairMustBeStored() {
        return settings.isKeyPairMustBeStored();
    }


}
