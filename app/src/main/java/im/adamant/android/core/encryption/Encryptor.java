package im.adamant.android.core.encryption;

import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.TransactionMessage;
import im.adamant.android.core.entities.TransactionState;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.goterl.lazycode.lazysodium.LazySodium;
import com.goterl.lazycode.lazysodium.exceptions.SodiumException;
import com.goterl.lazycode.lazysodium.interfaces.Sign;
import com.goterl.lazycode.lazysodium.utils.KeyPair;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


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

    public TransactionState encryptState(String key, String stringifiedState, String mySecretKey){
        TransactionState state = null;

        byte[] nonceBytes = sodium.randomBytesBuf(NONCE_LENGHT);

        try {
            String cryptoHashSha256 = sodium.cryptoHashSha256(mySecretKey);
            byte[] curveSecretKey = new byte[Sign.CURVE25519_SECRETKEYBYTES];
            sodium.convertSecretKeyEd25519ToCurve25519(curveSecretKey, cryptoHashSha256.getBytes());
            String encryptedMessage = sodium.cryptoSecretBoxEasy(
                    stringifiedState,
                    nonceBytes,
                    LazySodium.toHex(curveSecretKey)
            );

            JsonObject json = new JsonObject();
            json.add("message", new JsonPrimitive(encryptedMessage));
            json.add("nonce", new JsonPrimitive(LazySodium.toHex(nonceBytes)));

            state = new TransactionState();
            state.setKey(key);
            state.setType(0);
            state.setValue(json.toString());

        } catch (SodiumException e) {
            e.printStackTrace();
        }

        return state;
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

        } catch ( NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return sign;
    }

    public String generateRandomString() {
        return Double.toString(Math.random())
                .substring(0, 35)
                .replace("/[^a-z]+/g", "")
                .substring(0, (int) Math.ceil(Math.random() * 10));
    }

}
