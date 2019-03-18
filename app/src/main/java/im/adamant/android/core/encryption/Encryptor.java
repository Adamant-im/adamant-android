package im.adamant.android.core.encryption;

import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.TransactionMessage;
import im.adamant.android.core.entities.TransactionState;
import im.adamant.android.core.exceptions.InvalidValueForKeyValueStorage;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.goterl.lazycode.lazysodium.LazySodium;
import com.goterl.lazycode.lazysodium.exceptions.SodiumException;
import com.goterl.lazycode.lazysodium.interfaces.Sign;
import com.goterl.lazycode.lazysodium.utils.KeyPair;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Encryptor {
    private final int NONCE_LENGTH = 24;
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

    //TODO: It may be better to throw exceptions so that the client can handle them in its own way.
    public TransactionMessage encryptMessage(String message, String recipientPublicKey, String mySecretKey) {
        TransactionMessage transactionMessage = null;
        try {

            byte[] nonceBytes = sodium.randomBytesBuf(NONCE_LENGTH);

            KeyPair ed25519KeyPair = new KeyPair(recipientPublicKey, mySecretKey);
            KeyPair curve25519KeyPair = sodium.convertKeyPairEd25519ToCurve25519(ed25519KeyPair);

            String ecryptedMessage = sodium.cryptoBoxEasy(message, nonceBytes, curve25519KeyPair);

            transactionMessage = new TransactionMessage();
            transactionMessage.setMessage(ecryptedMessage.toLowerCase());
            transactionMessage.setOwnMessage(Hex.bytesToHex(nonceBytes));

        }catch (SodiumException e){
            e.printStackTrace();
        }

        return transactionMessage;
    }

    public TransactionState encryptState(String key, String stringifiedState, String mySecretKey){
        TransactionState state = null;

        byte[] nonceBytes = sodium.randomBytesBuf(NONCE_LENGTH);

        String  randomString = generateRandomString();
        stringifiedState = randomString + stringifiedState + randomString;

        try {
            byte[] cryptoHashSha256 = createCryptoHashSha256FromPrivateKey(mySecretKey);
            byte[] curveSecretKey = new byte[Sign.CURVE25519_SECRETKEYBYTES];

            sodium.convertSecretKeyEd25519ToCurve25519(curveSecretKey, cryptoHashSha256);
            String encryptedMessage = sodium.cryptoSecretBoxEasy(
                    stringifiedState,
                    nonceBytes,
                    LazySodium.toHex(curveSecretKey)
            );

            JsonObject json = new JsonObject();
            json.add("message", new JsonPrimitive(encryptedMessage.toLowerCase()));
            json.add("nonce", new JsonPrimitive(LazySodium.toHex(nonceBytes).toLowerCase()));

            state = new TransactionState();
            state.setKey(key);
            state.setType(0);
            state.setValue(json.toString());

        } catch (SodiumException e) {
            e.printStackTrace();
        }

        return state;
    }

    public JsonElement decryptState(TransactionState encryptedState, String mySecretKey) throws InvalidValueForKeyValueStorage {
        JsonElement jsonNode = null;

        JsonParser parser = new JsonParser();
        JsonObject encryptedStateValue = parser.parse(encryptedState.getValue()).getAsJsonObject();

        JsonElement messageJsonElement = encryptedStateValue.get("message");
        JsonElement nonceJsonElement = encryptedStateValue.get("nonce");

        if (messageJsonElement == null || nonceJsonElement == null){
            throw new InvalidValueForKeyValueStorage();
        }

        String message = messageJsonElement.getAsString();
        String nonce = nonceJsonElement.getAsString();

        try {
            byte[] cryptoHashSha256 = createCryptoHashSha256FromPrivateKey(mySecretKey);

            byte[] curveSecretKey = new byte[Sign.CURVE25519_SECRETKEYBYTES];
            sodium.convertSecretKeyEd25519ToCurve25519(curveSecretKey, cryptoHashSha256);

            byte[] nonceBytes = LazySodium.toBin(nonce);
            String curveSecretString = Hex.bytesToHex(curveSecretKey);

            String decryptedMessage = sodium.cryptoSecretBoxOpenEasy(
                    message,
                    nonceBytes,
                    curveSecretString
            );

            int from = decryptedMessage.indexOf('{');
            int to = decryptedMessage.lastIndexOf('}');

            if (from < 0 || to < 0) {
                throw new InvalidValueForKeyValueStorage();
            }

            String payloadDecryptedString = decryptedMessage.substring(from, to + 1);

            //TODO: You need to handle json in KvsHelper, otherwise there is a leak of responsibility between the encrypt method and decrypt.
            JsonElement jsonElement = parser.parse(payloadDecryptedString);

            if (jsonElement == null){throw new InvalidValueForKeyValueStorage();}

            jsonNode = jsonElement;
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return jsonNode;
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

    public String generateRandomString() { ;
        return Long.toString((long)(Math.random() * 10_000_000_000_000L), 36)
                .replace("/[^a-z]+/g", "");
    }

    private byte[] createCryptoHashSha256FromPrivateKey(String mySecretKey) {
        mySecretKey = mySecretKey.toLowerCase();
        byte[] cryptoHashSha256 = new byte[0];
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            cryptoHashSha256 = digest.digest(Hex.encodeStringToHexArray(mySecretKey));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return cryptoHashSha256;
    }
}
