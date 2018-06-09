package com.dremanovich.adamant_android.core.encryption;

import android.util.Log;

import com.goterl.lazycode.lazysodium.exceptions.SodiumException;
import com.goterl.lazycode.lazysodium.interfaces.Sign;
import com.goterl.lazycode.lazysodium.utils.KeyPair;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import io.github.novacrypto.bip39.MnemonicGenerator;
import io.github.novacrypto.bip39.SeedCalculator;
import io.github.novacrypto.bip39.Words;
import io.github.novacrypto.bip39.wordlists.English;

import static com.dremanovich.adamant_android.core.encryption.Hex.bytesToHex;
import static com.dremanovich.adamant_android.core.encryption.Hex.encodeStringToHexArray;

public class KeyGenerator {
    private MnemonicGenerator mnemonicGenerator;
    private SeedCalculator seedCalculator;
    private Sign.Lazy sodium;

    public KeyGenerator(SeedCalculator seedCalculator, MnemonicGenerator mnemonicGenerator, Sign.Lazy sodium) {
        this.mnemonicGenerator = mnemonicGenerator;
        this.seedCalculator = seedCalculator;
        this.sodium = sodium;
    }

    public KeyPair getKeyPairFromPassPhrase(String passPhrase) {
        KeyPair pair = null;

        try {

            byte[] blankCalculatedSeed = seedCalculator.calculateSeed(passPhrase, "");
            String seedString = bytesToHex(blankCalculatedSeed);

            byte[] seedForHash = encodeStringToHexArray(seedString);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] seed = digest.digest(seedForHash);

            pair = sodium.cryptoSignSeedKeypair(seed);

        } catch (NoSuchAlgorithmException | SodiumException e) {
            Log.e("KeyGenerator", e.getMessage(), e);
        }

        return pair;
    }

    public CharSequence generateNewPassphrase() {
        try {
            //TODO: Protect all Strings. See: https://medium.com/@_west_on/protecting-strings-in-jvm-memory-84c365f8f01c
            StringBuilder secure = new StringBuilder();
            byte[] entropy = new byte[Words.TWELVE.byteLength()];
            new SecureRandom().nextBytes(entropy);
            new MnemonicGenerator(English.INSTANCE)
                    .createMnemonic(entropy, secure::append);
            Arrays.fill(entropy, (byte) 0);

            return secure;
        } catch (Exception ex){
            ex.printStackTrace();
            return "";
        }
    }

}
