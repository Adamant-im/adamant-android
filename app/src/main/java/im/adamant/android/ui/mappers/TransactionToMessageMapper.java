package im.adamant.android.ui.mappers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.Encryptor;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.TransactionMessage;
import im.adamant.android.core.entities.transaction_assets.TransactionChatAsset;
import im.adamant.android.helpers.PublicKeyStorage;
import im.adamant.android.ui.messages_support.entities.AbstractMessage;

import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.builders.MessageBuilder;
import im.adamant.android.ui.messages_support.factories.MessageFactory;
import im.adamant.android.ui.messages_support.factories.MessageFactoryProvider;
import io.reactivex.functions.Function;

public class TransactionToMessageMapper implements Function<Transaction, AbstractMessage> {
    private Encryptor encryptor;
    private PublicKeyStorage publicKeyStorage;
    private AdamantApiWrapper api;
    private MessageFactoryProvider factoryProvider;

    public TransactionToMessageMapper(
            Encryptor encryptor,
            PublicKeyStorage publicKeyStorage,
            AdamantApiWrapper api,
            MessageFactoryProvider factoryProvider
    ) {
        this.encryptor = encryptor;
        this.publicKeyStorage = publicKeyStorage;
        this.api = api;
        this.factoryProvider = factoryProvider;
    }

    //TODO: Refactor this. The length of the method is too long.
    @Override
    public AbstractMessage apply(Transaction transaction) throws Exception {
        AbstractMessage message = null;

        if (!api.isAuthorized()){
            throw new Exception("You are not authorized.");
        }

        String ownAddress = api.getAccount().getAddress();
        String ownSecretKey = api.getKeyPair().getSecretKeyString().toLowerCase();

        boolean iRecipient = ownAddress.equalsIgnoreCase(transaction.getRecipientId());
        String companionId = (iRecipient) ? transaction.getSenderId() : transaction.getRecipientId();

        String decryptedMessage = decryptMessage(transaction, iRecipient, ownSecretKey);

        MessageFactory messageFactory = factoryProvider.getFactoryByType(
                detectMessageType(transaction, decryptedMessage)
        );
        MessageBuilder messageBuilder = messageFactory.getMessageBuilder();

        message = messageBuilder.build(
                transaction,
                decryptedMessage,
                !iRecipient,
                transaction.getUnixTimestamp(),
                companionId,
                "" //Detect by transaction
            );


        return message;
    }

    private String decryptMessage(Transaction transaction, boolean iRecipient, String ownSecretKey) {
        String decryptedMessage = "";

        TransactionMessage transactionMessage = getTransactionMessage(transaction);
        if (transactionMessage == null){return decryptedMessage;}

        TransactionChatAsset chatAsset = (TransactionChatAsset) transaction.getAsset();
        String encryptedMessage = chatAsset.getChat().getMessage();
        String encryptedNonce = chatAsset.getChat().getOwnMessage();
        String senderPublicKey = transaction.getSenderPublicKey();

        if (iRecipient){
            decryptedMessage = encryptor.decryptMessage(
                    encryptedMessage,
                    encryptedNonce,
                    senderPublicKey,
                    ownSecretKey
            );
        } else {
            String recipientPublicKey = publicKeyStorage.getPublicKey(transaction.getRecipientId());
            decryptedMessage = encryptor.decryptMessage(
                    encryptedMessage,
                    encryptedNonce,
                    recipientPublicKey,
                    ownSecretKey
            );
        }

        return decryptedMessage;
    }

    private SupportedMessageListContentType detectMessageType(Transaction transaction, String decryptedMessage) {
        TransactionMessage transactionMessage = getTransactionMessage(transaction);

        if (transactionMessage != null){
            switch (transactionMessage.getType()){
                case TransactionMessage.BASE_MESSAGE_TYPE : {
                    return SupportedMessageListContentType.ADAMANT_BASIC;
                }
                case TransactionMessage.RICH_MESSAGE_TYPE: {
                    String richType = getRichType(decryptedMessage);
                    switch (richType){
                        case "eth_transaction":
                          return SupportedMessageListContentType.ETHEREUM_TRANSFER;
                    }
                }
            }
        }

        return SupportedMessageListContentType.FALLBACK;
    }

    private String getRichType(String decryptedMessage) {
        String type = "undefined";
        try {
            JsonElement element = new JsonParser().parse(decryptedMessage);
            JsonObject  object = element.getAsJsonObject();

            JsonPrimitive reachType = object.getAsJsonPrimitive("type");

            if (reachType != null){
                type = reachType.getAsString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return type;
    }

    private TransactionMessage getTransactionMessage(Transaction transaction) {

        if (transaction.getAsset() == null){ return null; }
        TransactionChatAsset chatAsset = (TransactionChatAsset) transaction.getAsset();
        if (chatAsset.getChat() == null){ return null; }

        return chatAsset.getChat();
    }

}
