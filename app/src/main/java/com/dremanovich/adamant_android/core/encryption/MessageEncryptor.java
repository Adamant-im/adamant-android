package com.dremanovich.adamant_android.core.encryption;

import com.goterl.lazycode.lazysodium.LazySodium;
import com.goterl.lazycode.lazysodium.exceptions.SodiumException;
import com.goterl.lazycode.lazysodium.utils.KeyPair;


public class MessageEncryptor {
    private LazySodium sodium;

    public MessageEncryptor(LazySodium sodium) {
        this.sodium = sodium;
    }

    public String decryptMessage(String message, String ownMessage, String senderPublicKey, String mySecretKey) {
        String decryptedMessage = "";

        byte[] nonceBytes = Hex.encodeStringToHexArray(ownMessage);

        try {
            KeyPair ed25519KeyPair = new KeyPair(senderPublicKey, mySecretKey);
            KeyPair curve25519KeyPair = sodium.convertKeyPairEd25519ToCurve25519(ed25519KeyPair);

            decryptedMessage = sodium.cryptoBoxOpenEasy(message, nonceBytes, curve25519KeyPair);
        } catch (SodiumException e) {
            e.printStackTrace();
        }

        return decryptedMessage;
    }

}
