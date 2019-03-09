package im.adamant.android.core.encryption;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.ECParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Calendar;

import android.security.keystore.KeyProperties;
import android.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;

import im.adamant.android.core.exceptions.EncryptionException;

import static java.lang.Math.floor;

public class KeyStoreCipher {
    public static final int KEY_SIZE = 4096;
    public static final String ALGORITHM = "RSA";
    public static final String PROVIDER = "AndroidKeyStore";
    public static final String KEY_ALIAS_PREFIX = "AdamantKeystoreAlias_";
    public static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";

    private static KeyStore androidKeyStore;

    private Gson gson;
    private Context context;

    public KeyStoreCipher(Gson gson, Context context) {
        this.gson = gson;
        this.context = context;

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

        byte[] bytes = data.toString().getBytes();
        long maxContentSize = Math.round(floor(KEY_SIZE / 8) - 11);
        if(bytes.length > maxContentSize){
            throw new EncryptionException("Serialized object to long (maximum " + maxContentSize + " bytes)");
        }

        alias = KEY_ALIAS_PREFIX + alias;

        KeyPair securityKeyPair = getKeyPair(alias);

        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, securityKeyPair.getPublic());

        byte[] encryptedBytes = cipher.doFinal(bytes);

        return Base64.encodeToString(encryptedBytes, Base64.NO_PADDING | Base64.NO_WRAP);
    }

    public CharSequence decrypt(String alias, String object) throws NoSuchProviderException, InvalidAlgorithmParameterException {
        String decryptedString = "";

        byte[] encryptedData = Base64.decode(object, Base64.NO_PADDING | Base64.NO_WRAP);

        try {
            alias = KEY_ALIAS_PREFIX + alias;

            KeyPair securityKeyPair = getKeyPair(alias);

            final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, securityKeyPair.getPrivate());

            final byte[] decodedData = cipher.doFinal(encryptedData);
            decryptedString = new String(decodedData, StandardCharsets.UTF_8);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return decryptedString;
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
}
