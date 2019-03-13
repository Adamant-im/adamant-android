package im.adamant.android.core.encryption;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.goterl.lazycode.lazysodium.LazySodium;
import com.goterl.lazycode.lazysodium.Sodium;
import com.goterl.lazycode.lazysodium.exceptions.SodiumException;
import com.goterl.lazycode.lazysodium.interfaces.PwHash;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.ECParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import android.security.keystore.KeyProperties;
import android.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;

import im.adamant.android.core.exceptions.EncryptionException;
import im.adamant.android.helpers.CharSequenceHelper;

import static java.lang.Math.floor;

public class KeyStoreCipher {
    public static final String LARGE_DATA_TYPE = "LD";
    public static final String SMALL_DATA_TYPE = "SD";

    public static final int SECURE_HASH_LEN = 128;
    public static final int KEY_SIZE = 4096;
    public static final int MAX_BLOCK_SIZE = ((KEY_SIZE >> 3) - 11);
    public static final String ALGORITHM = "RSA";
    public static final String PROVIDER = "AndroidKeyStore";
    public static final String KEY_ALIAS_PREFIX = "AdamantKeystoreAlias_";
    public static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";

    private static KeyStore androidKeyStore;

    private LazySodium sodium;
    private Context context;
    private JsonParser parser;

    public KeyStoreCipher(LazySodium sodium, JsonParser parser, Context context) {
        this.context = context;
        this.sodium = sodium;
        this.parser = parser;

        if (androidKeyStore == null){
            KeyStore instance = null;
            try {
                instance = KeyStore.getInstance(PROVIDER);
                if (instance != null){
                    instance.load(null);
                }
            } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
                e.printStackTrace();
            }

            androidKeyStore = instance;
        }
    }

    public String encrypt(String alias, CharSequence data) throws Exception {
        if (data == null) {throw new EncryptionException("Data for encryption is null");}

        try {
            byte[] bytes = data.toString().getBytes();

            if(bytes.length > MAX_BLOCK_SIZE){
                return LARGE_DATA_TYPE + encryptLargeBlock(alias, data);
            } else {
                return SMALL_DATA_TYPE + encryptSmallBlock(alias, bytes);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new EncryptionException(ex.getMessage());
        }
    }

    private String encryptSmallBlock(String alias, byte[] bytes) throws Exception {
        alias = KEY_ALIAS_PREFIX + alias;

        KeyPair securityKeyPair = getKeyPair(alias);

        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, securityKeyPair.getPublic());

        byte[] encryptedBytes = cipher.doFinal(bytes);

        return Base64.encodeToString(encryptedBytes, Base64.NO_PADDING | Base64.NO_WRAP);
    }

    private String encryptLargeBlock(String alias, CharSequence sequence) throws Exception {
        alias = KEY_ALIAS_PREFIX + alias;

        KeyPair securityKeyPair = getKeyPair(alias);

        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, securityKeyPair.getPublic());

        JsonArray blocks = new JsonArray();

        int index = 0;
        int maxChars = MAX_BLOCK_SIZE >>> 1;

        while (index < sequence.length()) {
            int count = maxChars;
            int endPosition = index + count;

            if (endPosition >= sequence.length() - 1) {
                count = sequence.length()  - index;
                endPosition = index + count;
            }

            CharSequence charSequence = sequence.subSequence(index, endPosition);
            blocks.add(
                    Base64.encodeToString(
                            cipher.doFinal(
                                    charSequence.toString().getBytes()
                            ),
                            Base64.NO_PADDING | Base64.NO_WRAP
                    )
            );

            index += count;
        }

        return blocks.toString();
    }

    public CharSequence decrypt(String alias, String object) throws Exception {
        String dataSizeType = object.substring(0, 2);
        String data = object.substring(2);
        try {
            switch (dataSizeType) {
                case SMALL_DATA_TYPE: {
                    return decryptSmallBlock(alias, data);
                }
                case LARGE_DATA_TYPE: {
                    return decryptLargeBlock(alias, data);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new EncryptionException(ex.getMessage());
        }

        return "";
    }

    private CharSequence decryptSmallBlock(String alias, String object) throws Exception {
        String decryptedString = "";

        byte[] encryptedData = Base64.decode(object, Base64.NO_PADDING | Base64.NO_WRAP);

        alias = KEY_ALIAS_PREFIX + alias;

        KeyPair securityKeyPair = getKeyPair(alias);

        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, securityKeyPair.getPrivate());

        final byte[] decodedData = cipher.doFinal(encryptedData);

        //TODO: Must be StringBuffer not String
        decryptedString = new String(decodedData, StandardCharsets.UTF_8);

        return decryptedString;
    }

    private CharSequence decryptLargeBlock(String alias, String object) throws Exception {
        StringBuilder builder = new StringBuilder();

        alias = KEY_ALIAS_PREFIX + alias;

        KeyPair securityKeyPair = getKeyPair(alias);

        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, securityKeyPair.getPrivate());

        JsonElement largeElement = parser.parse(object);
        JsonArray blocks = largeElement.getAsJsonArray();

       for (JsonElement blockElement : blocks) {
           String blockData = blockElement.getAsString();
           byte[] bytesInBlock = Base64.decode(blockData, Base64.NO_PADDING | Base64.NO_WRAP);
           byte[] decodedData = cipher.doFinal(bytesInBlock);
           builder.append(new String(decodedData, StandardCharsets.UTF_8));
       }

        return builder.toString();
    }

    private KeyPair getKeyPair(String alias) throws NoSuchAlgorithmException , NoSuchProviderException, InvalidAlgorithmParameterException {

        PrivateKey privateKey = null;
        PublicKey publicKey = null;

        if (androidKeyStore != null){
            try {
                privateKey = (PrivateKey) androidKeyStore.getKey(alias, null);
                Certificate certificate = androidKeyStore.getCertificate(alias);

                if (certificate != null){
                    publicKey = certificate
                            .getPublicKey();
                }

            } catch (UnrecoverableKeyException | KeyStoreException ex) {
                ex.printStackTrace();
            }

        }

        if (privateKey != null && publicKey != null){
            return new KeyPair(publicKey, privateKey);
        } else {
            return generateKeyPair(alias);
        }
    }

    public boolean validateSign(String alias, CharSequence data, CharSequence pinCodeHash, CharSequence signForVerify) {
        if (data == null || pinCodeHash == null){return false;}
        CharSequence baseForSign = CharSequenceHelper.concat(pinCodeHash, data);
        byte[] jsonBytes = baseForSign.toString().getBytes();

        Signature sig = null;
        try {
            alias = KEY_ALIAS_PREFIX + alias;

            KeyPair securityKeyPair = getKeyPair(alias);

            sig = Signature.getInstance("SHA1WithRSA");

            sig.initVerify(securityKeyPair.getPublic());
            sig.update(jsonBytes);

            return sig.verify(signForVerify.toString().getBytes());
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
            e.printStackTrace();
        }

        return false;
    }

//    public SecureHash secureHash(CharSequence data) {
//        byte[] salt = sodium.randomBytesBuf(PwHash.SALTBYTES);
//
//        return secureHash(data, LazySodium.toHex(salt));
//    }

    public String secureHash(CharSequence data) throws EncryptionException {
//        byte[] salt = sodium.randomBytesBuf(PwHash.SALTBYTES);

        // Can also use any number from PwHash.BYTES_MIN to PwHash.BYTES_MAX instead of "SECURE_HASH_LEN".
        // But be aware that your device may run out of memory the larger the value you supply.
//        byte[] outputHash = sodium.randomBytesBuf(SECURE_HASH_LEN);
//        int outputHashLen = outputHash.length;

//        byte[] dataBytes = data.toString().getBytes();
//        int passwordLen = dataBytes.length;

//        sodium.getSodium().crypto_pwhash(outputHash,
//                outputHashLen,
//                dataBytes,
//                passwordLen,
//                salt,
//                PwHash.OPSLIMIT_SENSITIVE,
//                PwHash.MEMLIMIT_MODERATE,
//                PwHash.Alg.getDefault().getValue());

        try {
            return sodium.cryptoPwHashStr(data.toString(), PwHash.OPSLIMIT_SENSITIVE, PwHash.MEMLIMIT_MODERATE);
        } catch (SodiumException e) {
            e.printStackTrace();
            throw new EncryptionException("Hash not created");
        }


    }

    public boolean verifyHash(String hash, String data) {
        return sodium.cryptoPwHashStrVerify(hash, data);
    }


    //TODO: Protect store via pincode and fingerprint
    private KeyPair generateKeyPair(String alias) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            initGeneratorWithKeyGenParameterSpec(generator, alias);
        } else {
            initGeneratorWithKeyPairGeneratorSpec(generator, alias);
        }

        return generator.generateKeyPair();
    }

    private void initGeneratorWithKeyPairGeneratorSpec(KeyPairGenerator generator, String alias) throws InvalidAlgorithmParameterException {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.YEAR, 200);

        KeyPairGeneratorSpec.Builder builder = new KeyPairGeneratorSpec
                .Builder(context)
                .setAlias(alias)
                .setKeySize(KEY_SIZE)
                .setSerialNumber(BigInteger.ONE)
                .setSubject(new X500Principal("CN=" + alias + " CA Certificate"))
                .setStartDate(startDate.getTime())
                .setEndDate(endDate.getTime());

        generator.initialize(builder.build());
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initGeneratorWithKeyGenParameterSpec(KeyPairGenerator generator, String alias) throws InvalidAlgorithmParameterException {
        KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec
                .Builder(alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                .setKeySize(KEY_SIZE)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1);

        generator.initialize(builder.build());
    }

//    public static class SecureHash {
//        private String salt;
//        private String hash;
//
//        public SecureHash(String salt, String hash) {
//            this.salt = salt;
//            this.hash = hash;
//        }
//
//        public String getSalt() {
//            return salt;
//        }
//
//        public String getHash() {
//            return hash;
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (o == null || getClass() != o.getClass()) return false;
//            SecureHash that = (SecureHash) o;
//            return Objects.equals(salt, that.salt) &&
//                    Objects.equals(hash, that.hash);
//        }
//
//        @Override
//        public int hashCode() {
//            return Objects.hash(salt, hash);
//        }
//    }
}
