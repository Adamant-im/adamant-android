package im.adamant.android.interactors;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import im.adamant.android.Constants;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.Encryptor;
import im.adamant.android.core.encryption.KeyStoreCipher;
import im.adamant.android.helpers.Settings;
import im.adamant.android.rx.ObservableRxList;
import im.adamant.android.core.entities.ServerNode;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class SaveKeypairInteractor {
    private Gson gson;
    private Settings settings;
    private AdamantApiWrapper api;
    private Encryptor encryptor;

    public SaveKeypairInteractor(
            Gson gson,
            Settings settings,
            AdamantApiWrapper api,
            Encryptor encryptor
    ) {
        this.settings = settings;
        this.api = api;
        this.encryptor = encryptor;
        this.gson = gson;
    }

    public Completable saveKeypair(String pincode) {
            return Completable.fromAction(() -> {
            String salt = settings.getSalt();

            if (salt.isEmpty()){
                salt = encryptor.generateRandomString() + encryptor.generateRandomString();
                settings.setSalt(salt);
            }

            if (!pincode.isEmpty() && !salt.isEmpty()){
                try {
                    if (api.isAuthorized()){
                        String keyData = gson.toJson(api.getKeyPair());
                        JsonObject jsonObject = encryptor.protectByPinCode(keyData, pincode, salt);
                        settings.setAccountKeypair(jsonObject.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                dropKeyPair();
            }
        }).subscribeOn(Schedulers.computation());
    }

    public void dropKeyPair() {
        settings.setAccountKeypair("");
        settings.setKeyPairMustBeStored(false);
    }

    public boolean isKeyPairMustBeStored() {
        return settings.isKeyPairMustBeStored();
    }


}
