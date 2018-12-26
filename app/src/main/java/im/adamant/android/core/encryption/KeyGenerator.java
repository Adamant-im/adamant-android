package im.adamant.android.core.encryption;

import com.goterl.lazycode.lazysodium.utils.KeyPair;

public interface KeyGenerator {
    KeyPair getKeyPairFromPassPhrase(String passPhrase);
}
