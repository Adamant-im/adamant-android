package im.adamant.android.ui.mappers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.Encryptor;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.TransactionMessage;
import im.adamant.android.helpers.PublicKeyStorage;
import im.adamant.android.ui.entities.Message;

import io.reactivex.functions.Function;

public class TransactionToMessageMapper implements Function<Transaction, Message> {
    private Encryptor encryptor;
    private PublicKeyStorage publicKeyStorage;
    private AdamantApiWrapper api;

    public TransactionToMessageMapper(
            Encryptor encryptor,
            PublicKeyStorage publicKeyStorage,
            AdamantApiWrapper api
    ) {
        this.encryptor = encryptor;
        this.publicKeyStorage = publicKeyStorage;
        this.api = api;
    }

    //TODO: Refactor this. The length of the method is too long.
    @Override
    public Message apply(Transaction transaction) throws Exception {
        Message message = null;

        if (api.getKeyPair() == null || api.getAccount() == null){
            throw new Exception("You are not authorized.");
        }

        String ownAddress = api.getAccount().getAddress();
        String ownSecretKey = api.getKeyPair().getSecretKeyString().toLowerCase();

        boolean iRecipient = ownAddress.equalsIgnoreCase(transaction.getRecipientId());
        String companionId = (iRecipient) ? transaction.getSenderId() : transaction.getRecipientId();

        String decryptedMessage = decryptMessage(transaction, iRecipient, ownSecretKey);

        message = new Message();
        message.setMessage(decryptedMessage);
        message.setiSay(ownAddress.equals(transaction.getSenderId()));
        message.setDate(messageMagicTimestamp(
                transaction.getTimestamp()
        ));
        message.setCompanionId(companionId);
        message.setProcessed(true);
        message.setTransactionId(transaction.getId());

        //Fallbacks
        boolean existsAsset = (transaction.getAsset() != null) && (transaction.getAsset().getChat() != null);
        if (existsAsset){
            TransactionMessage transactionMessage = transaction.getAsset().getChat();

            if (transactionMessage.getType() == TransactionMessage.REACH_MESSAGE_TYPE){
                addFallback(message);
            }
        }

        return message;
    }

    private long messageMagicTimestamp(long receivedTimestamp) {
        //Date magic transformations, see PWA code. File: lib/formatters.js line 42. Symbolically ;)
        return (receivedTimestamp * 1000L) + AdamantApi.BASE_TIMESTAMP;
    }

    private String decryptMessage(Transaction transaction, boolean iRecipient, String ownSecretKey) {
        String decryptedMessage = "";

        if (transaction.getAsset() == null){ return decryptedMessage; }
        if (transaction.getAsset().getChat() == null){ return decryptedMessage; }

        String encryptedMessage = transaction.getAsset().getChat().getMessage();
        String encryptedNonce = transaction.getAsset().getChat().getOwnMessage();
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

    //TODO: Develop a architecture of processing different types of messages

    private void addFallback(Message message) {
        try {
            message.setFallback(true);
            JsonElement jelement = new JsonParser().parse(message.getMessage());
            JsonObject  jobject = jelement.getAsJsonObject();

            JsonPrimitive textFallback = jobject.getAsJsonPrimitive("text_fallback");

            if (textFallback != null){
                message.setFallBackMessage(textFallback.getAsString());
            }

            JsonPrimitive reachType = jobject.getAsJsonPrimitive("type");

            if (reachType != null){
                message.setReachType(reachType.getAsString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
