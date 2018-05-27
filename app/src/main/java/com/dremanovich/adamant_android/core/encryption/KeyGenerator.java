package com.dremanovich.adamant_android.core.encryption;

import android.util.Log;

import com.goterl.lazycode.lazysodium.exceptions.SodiumException;
import com.goterl.lazycode.lazysodium.interfaces.Sign;
import com.goterl.lazycode.lazysodium.utils.KeyPair;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.github.novacrypto.bip39.SeedCalculator;

import static com.dremanovich.adamant_android.core.encryption.Hex.bytesToHex;
import static com.dremanovich.adamant_android.core.encryption.Hex.encodeStringToHexArray;

public class KeyGenerator {
    private SeedCalculator seedCalculator;
    private Sign.Lazy sodium;

    public KeyGenerator(SeedCalculator seedCalculator, Sign.Lazy sodium) {
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

}
