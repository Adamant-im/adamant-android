package im.adamant.android.core.encryption;

import com.goterl.lazycode.lazysodium.LazySodium;
import com.goterl.lazycode.lazysodium.Sodium;
import com.goterl.lazycode.lazysodium.exceptions.SodiumException;
import com.goterl.lazycode.lazysodium.interfaces.Sign;
import com.goterl.lazycode.lazysodium.utils.KeyPair;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.Normalizer;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import im.adamant.android.helpers.LoggerHelper;
import io.github.novacrypto.bip39.MnemonicGenerator;
import io.github.novacrypto.bip39.SeedCalculator;


public class LiskKeyGenerator implements KeyGenerator {
    public static final String KEY = "LiskKeyGenerator";

    private MnemonicGenerator mnemonicGenerator;
    private LazySodium sodium;

    public LiskKeyGenerator(MnemonicGenerator mnemonicGenerator, LazySodium sodium) {
        this.mnemonicGenerator = mnemonicGenerator;
        this.sodium = sodium;
    }

    @Override
    public KeyPair getKeyPairFromPassPhrase(String passPhrase) {
        KeyPair pair = null;
        try {
            byte[] seed = generateSeed(passPhrase, "adm");
            pair = sodium.cryptoSignSeedKeypair(seed);

            if (pair != null) {
                LoggerHelper.e("LISKPUBLIC", pair.getPublicKeyString().toLowerCase());
                LoggerHelper.e("LISKPRIVATE", pair.getSecretKeyString().toLowerCase());
            }

        } catch (SodiumException e) {
            LoggerHelper.e("LiskKeyGenerator", e.getMessage(), e);
        }

        return pair;
    }

    private byte[] generateSeed(String passphrase, String salt) {
        byte[] hash = new byte[0];
        String normalizedPassphrase = Normalizer.normalize(passphrase, Normalizer.Form.NFKD);
        String normalizedSalt = Normalizer.normalize(salt, Normalizer.Form.NFKD);

        char[] preparedPassphrase = normalizedPassphrase.toCharArray();
        byte[] preparedSalt = normalizedSalt.getBytes();

        try {
            PBEKeySpec spec = new PBEKeySpec(preparedPassphrase, preparedSalt, 2048, 32 * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            hash = skf.generateSecret(spec).getEncoded();

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return hash;
    }
}