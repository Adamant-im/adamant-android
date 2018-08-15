package im.adamant.android.core.encryption;

import android.util.Log;

import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.TransactionMessage;
import im.adamant.android.core.entities.TransactionState;
import im.adamant.android.core.exceptions.InvalidValueForKeyValueStorage;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.goterl.lazycode.lazysodium.LazySodium;
import com.goterl.lazycode.lazysodium.exceptions.SodiumException;
import com.goterl.lazycode.lazysodium.interfaces.Sign;
import com.goterl.lazycode.lazysodium.utils.KeyPair;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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

    public TransactionMessage encryptMessage(String message, String recipientPublicKey, String mySecretKey){
        TransactionMessage chat = null;
        try {

            byte[] nonceBytes = sodium.randomBytesBuf(NONCE_LENGTH);

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

        byte[] nonceBytes = sodium.randomBytesBuf(NONCE_LENGTH);

        String  randomString = generateRandomString();
        stringifiedState = randomString + "{payload: \"" + stringifiedState + "\"}" + randomString;

        try {
            String cryptoHashSha256 = sodium.cryptoHashSha256(mySecretKey.toLowerCase());
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

    public String decryptState(TransactionState encryptedState, String mySecretKey) throws InvalidValueForKeyValueStorage {
        String payloadString = "";

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
            mySecretKey = mySecretKey.toLowerCase();

            MessageDigest digest = null;
            byte[] cryptoHashSha256 = null;
            try {
                digest = MessageDigest.getInstance("SHA-256");
                cryptoHashSha256 = digest.digest(Hex.encodeStringToHexArray(mySecretKey));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

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

            JsonElement jsonElement = parser.parse(payloadDecryptedString);

            if (jsonElement == null){throw new InvalidValueForKeyValueStorage();}

            JsonObject payloadObject = jsonElement.getAsJsonObject();

            JsonElement payload = payloadObject.get("payload");
            if (payload == null){
                throw new InvalidValueForKeyValueStorage();
            }

            payloadString = payload.toString();

        }catch (SodiumException | JsonSyntaxException ex){
            ex.printStackTrace();
        }

        return payloadString;
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
