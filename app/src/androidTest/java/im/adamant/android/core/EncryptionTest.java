package im.adamant.android.core;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.goterl.lazycode.lazysodium.LazySodium;
import com.goterl.lazycode.lazysodium.LazySodiumAndroid;
import com.goterl.lazycode.lazysodium.SodiumAndroid;
import com.goterl.lazycode.lazysodium.utils.KeyPair;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Singleton;

import dagger.Provides;
import im.adamant.android.InstrumentedTestConstants;
import im.adamant.android.core.encryption.AdamantKeyGenerator;
import im.adamant.android.core.encryption.Encryptor;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.TransactionMessage;
import im.adamant.android.core.entities.TransactionState;
import im.adamant.android.core.entities.transaction_assets.TransactionChatAsset;
import im.adamant.android.ui.entities.Contact;
import io.github.novacrypto.bip39.MnemonicGenerator;
import io.github.novacrypto.bip39.SeedCalculator;
import io.github.novacrypto.bip39.wordlists.English;

public class EncryptionTest {
    private Gson gson;
    private Encryptor encryptor;
    private AdamantKeyGenerator adamantKeyGenerator;

    @Before
    public void setUp() {
        gson = new Gson();
        SodiumAndroid sodium = new SodiumAndroid();
        LazySodiumAndroid lazySodiumAndroid = new LazySodiumAndroid(sodium);
        encryptor = new Encryptor(lazySodiumAndroid);
        MnemonicGenerator mnemonicGenerator = new MnemonicGenerator(English.INSTANCE);

        SeedCalculator seedCalculator = new SeedCalculator();
        adamantKeyGenerator = new AdamantKeyGenerator(seedCalculator, mnemonicGenerator, lazySodiumAndroid);
    }

    @Test
    public void adamantKeypairTest() {
        KeyPair keyPairFromPassPhrase = adamantKeyGenerator.getKeyPairFromPassPhrase(InstrumentedTestConstants.PASSPHRASE);
        Assert.assertNotNull(keyPairFromPassPhrase);

        Assert.assertEquals(keyPairFromPassPhrase.getPublicKeyString().toLowerCase(), InstrumentedTestConstants.ADAMANT_PUBLIC_KEY);
        Assert.assertEquals(keyPairFromPassPhrase.getSecretKeyString().toLowerCase(), InstrumentedTestConstants.ADAMANT_SECRET_KEY);
    }

    @Test
    public void encryptAndDecryptMessageTest() {
        KeyPair pair = new KeyPair(InstrumentedTestConstants.ADAMANT_PUBLIC_KEY, InstrumentedTestConstants.ADAMANT_SECRET_KEY);

        TransactionMessage transactionMessage = encryptor.encryptMessage(
                "Hi human!",
                pair.getPublicKeyString(),
                pair.getSecretKeyString()
        );

        Assert.assertNotNull(transactionMessage);

        String decryptMessage = encryptor.decryptMessage(
                transactionMessage.getMessage(),
                transactionMessage.getOwnMessage(),
                pair.getPublicKeyString(), pair.getSecretKeyString()
        );

        Assert.assertEquals("Hi human!", decryptMessage);
    }

    @Test
    public void encryptAndDecryptStateTest() throws Exception {
        KeyPair pair = new KeyPair(InstrumentedTestConstants.ADAMANT_PUBLIC_KEY, InstrumentedTestConstants.ADAMANT_SECRET_KEY);

        Contact contact = new Contact();
        contact.setDisplayName("Test contact");

        JsonObject valueObject = new JsonObject();
        valueObject.add("payload", gson.toJsonTree(contact));
        String valueString = valueObject.toString();

        TransactionState encryptedState = encryptor.encryptState("test", valueString, pair.getSecretKeyString());
        Assert.assertNotNull(encryptedState);

        JsonElement jsonElement = encryptor.decryptState(encryptedState, pair.getSecretKeyString());

        Assert.assertEquals(valueString, jsonElement.toString());
    }

    @Test
    public void transactionSignatureTest() {
        KeyPair pair = new KeyPair(InstrumentedTestConstants.ADAMANT_PUBLIC_KEY, InstrumentedTestConstants.ADAMANT_SECRET_KEY);
        Transaction<TransactionChatAsset> transaction = provideTestTransaction();

        String transactionSignature = encryptor.createTransactionSignature(transaction, pair);

        Assert.assertEquals(
                "141ec23ee0622b91ca5da9a64210552ed9af9885c3f518392634dc2948bd171f86a7347f9b0336b2e43d28ef94ae7d24db723bc423bab9f32444803b9f50d409",
                transactionSignature
        );
    }

    private Transaction<TransactionChatAsset> provideTestTransaction() {
        Transaction<TransactionChatAsset> transaction = new Transaction<>();
        transaction.setId("12340172291579192276");
        transaction.setHeight(8260197);
        transaction.setBlockId("16909274633147809954");
        transaction.setType(Transaction.CHAT_MESSAGE);
        transaction.setTimestamp(48571584);
        transaction.setSenderPublicKey("25317d2808deaf2d636f84380a7cb2014e2f510ea21c23f241b69dda4cea4e0e");
        transaction.setSenderId("U7579307846948829803");
        transaction.setRecipientId("U1119781441708645832");
        transaction.setAmount(0);
        transaction.setFee(100000);

        TransactionMessage transactionMessage = new TransactionMessage();
        transactionMessage.setMessage("5a04836287022c407cf239cf64b0cfdc1baaec28d7");
        transactionMessage.setOwnMessage("20a142aecd4d17ad4c77896c16460fc7ca1404c8b3eb4e8c");
        transactionMessage.setType(TransactionMessage.BASE_MESSAGE_TYPE);

        TransactionChatAsset asset = new TransactionChatAsset();
        asset.setChat(transactionMessage);

        transaction.setAsset(asset);

        return transaction;
    }
}
