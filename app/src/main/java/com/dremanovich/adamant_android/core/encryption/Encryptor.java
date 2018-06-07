package com.dremanovich.adamant_android.core.encryption;

import android.util.Log;

import com.dremanovich.adamant_android.core.entities.Transaction;
import com.dremanovich.adamant_android.core.entities.TransactionMessage;
import com.goterl.lazycode.lazysodium.LazySodium;
import com.goterl.lazycode.lazysodium.exceptions.SodiumException;
import com.goterl.lazycode.lazysodium.interfaces.Sign;
import com.goterl.lazycode.lazysodium.utils.KeyPair;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class Encryptor {
    private final int NONCE_LENGHT = 24;
    private LazySodium sodium;

    public Encryptor(LazySodium sodium) {
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

    public TransactionMessage encryptMessage(String message, String recipientPublicKey, String mySecretKey){
        TransactionMessage chat = null;
        try {

            byte[] nonceBytes = sodium.randomBytesBuf(NONCE_LENGHT);

            KeyPair ed25519KeyPair = new KeyPair(recipientPublicKey, mySecretKey);
            KeyPair curve25519KeyPair = sodium.convertKeyPairEd25519ToCurve25519(ed25519KeyPair);

            String ecryptedMessage = sodium.cryptoBoxEasy(message, nonceBytes, curve25519KeyPair);

            chat = new TransactionMessage();
            chat.setMessage(ecryptedMessage.toLowerCase());
            chat.setOwnMessage(Hex.bytesToHex(nonceBytes));

        }catch (SodiumException e){
            e.printStackTrace();
        }

        return chat;
    }

    public String createTransactionSignature(Transaction transaction, KeyPair keyPair) {
        String sign = "";
        byte[] transactionBytes = transaction.getBytesDigest();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(transactionBytes);

            byte[] signBytes = new byte[Sign.BYTES];

            sodium.getSodium().crypto_sign_detached(
                    signBytes,
                    null,
                    hash,
                    (long)hash.length,
                    keyPair.getSecretKey()
            );

            sign = Hex.bytesToHex(signBytes);

            Log.e("UBITES", Arrays.toString(Hex.getUnsignedBytes(transactionBytes)));
            Log.e("hashbytes", Arrays.toString(Hex.getUnsignedBytes(hash)));
            Log.e("SignBytes", Arrays.toString(Hex.getUnsignedBytes(signBytes)));
            Log.e("SIGN", sign);

        } catch ( NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return sign;
    }

}
