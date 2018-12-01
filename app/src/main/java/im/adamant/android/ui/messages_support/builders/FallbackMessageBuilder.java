package im.adamant.android.ui.messages_support.builders;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import im.adamant.android.core.entities.Transaction;
import im.adamant.android.ui.messages_support.entities.FallbackMessage;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;

public class FallbackMessageBuilder implements MessageBuilder<FallbackMessage> {

    private static class MessageDescription {
        String type;
        @SerializedName("text_fallback")
        String textFallback;
    }

    @Override
    public FallbackMessage build(
            Transaction transaction,
            String decryptedMessage,
            boolean isISayed,
            long date,
            String companionId,
            String ownerPublicKey
    ) {
        FallbackMessage message = new FallbackMessage();
        message.setSupportedType(SupportedMessageListContentType.FALLBACK);
        message.setiSay(isISayed);
        message.setTimestamp(date);
        message.setCompanionId(companionId);
        message.setOwnerPublicKey(ownerPublicKey);

        if (transaction != null){
            message.setOwnerPublicKey(transaction.getSenderPublicKey());
            message.setProcessed(true);
            message.setTransactionId(transaction.getId());
        }

        parseFallback(message, decryptedMessage);

        return message;
    }

    private void parseFallback(FallbackMessage messageContainer, String decryptedMessage) {
        Gson gson = new Gson();
        try {
            MessageDescription description = gson.fromJson(decryptedMessage, MessageDescription.class);
            messageContainer.setFallbackMessage(description.textFallback);
            messageContainer.setFallbackType(description.type);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
