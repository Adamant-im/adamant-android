package im.adamant.android.ui.messages_support.builders;

import im.adamant.android.core.entities.Transaction;
import im.adamant.android.ui.entities.messages.AdamantBasicMessage;
import im.adamant.android.ui.messages_support.SupportedMessageTypes;

public class AdamantBasicMessageBuilder implements MessageBuilder<AdamantBasicMessage> {
    @Override
    public AdamantBasicMessage build(Transaction transaction, String decryptedMessage, boolean isISayed, long date, String companionId) {
        AdamantBasicMessage message = new AdamantBasicMessage();
        message.setSupportedType(SupportedMessageTypes.ADAMANT_BASIC);
        message.setText(decryptedMessage);
        message.setiSay(isISayed);
        message.setDate(date);
        message.setCompanionId(companionId);
        message.setProcessed(true);
        message.setTransactionId(transaction.getId());

        return message;
    }
}
