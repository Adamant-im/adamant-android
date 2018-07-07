package im.adamant.android.ui.messages_support.builders;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.SerializedName;

import im.adamant.android.core.entities.Transaction;
import im.adamant.android.ui.entities.messages.FallbackMessage;
import im.adamant.android.ui.messages_support.SupportedMessageTypes;

public class FallbackMessageBuilder implements MessageBuilder<FallbackMessage> {

    private static class MessageDescription {
        String type;
        @SerializedName("text_fallback")
        String textFallback;
    }

    @Override
    public FallbackMessage build(Transaction transaction, String decryptedMessage, boolean isISayed, long date, String companionId) {
        FallbackMessage message = new FallbackMessage();
        message.setSupportedType(SupportedMessageTypes.FALLBACK);
        message.setiSay(isISayed);
        message.setDate(date);
        message.setCompanionId(companionId);
        message.setProcessed(true);
        message.setTransactionId(transaction.getId());

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
