package im.adamant.android.ui.messages_support.builders;

import im.adamant.android.core.entities.Transaction;
import im.adamant.android.ui.messages_support.entities.AdamantBasicMessage;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;

public class AdamantBasicMessageBuilder implements MessageBuilder<AdamantBasicMessage> {
    @Override
    public AdamantBasicMessage build(
            Transaction transaction,
            String decryptedMessage,
            boolean isISayed,
            long date,
            String companionId,
            String ownerPublicKey
    ) {
        AdamantBasicMessage message = new AdamantBasicMessage();
        message.setSupportedType(SupportedMessageListContentType.ADAMANT_BASIC);
        message.setText(decryptedMessage);
        message.setiSay(isISayed);
        message.setTimestamp(date);
        message.setCompanionId(companionId);
        message.setOwnerPublicKey(ownerPublicKey);

        if (transaction != null){
            message.setOwnerPublicKey(transaction.getSenderPublicKey());

            message.setProcessed(true);
            message.setTransactionId(transaction.getId());
        }

        return message;
    }
}
