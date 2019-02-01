package im.adamant.android.helpers;

import io.reactivex.Flowable;
import io.reactivex.Single;

public interface PublicKeyStorage {
    String getPublicKey(String address);
    Flowable<String> getPublicKeyFlowable(String address);
}
