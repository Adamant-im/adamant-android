package im.adamant.android.ui.messages_support.builders;

import im.adamant.android.core.entities.Transaction;
import im.adamant.android.helpers.BalanceConvertHelper;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.entities.AdamantBasicMessage;
import im.adamant.android.ui.messages_support.entities.AdamantTransferMessage;

public class AdamantTransferMessageBuilder implements MessageBuilder<AdamantTransferMessage> {
    @Override
    public AdamantTransferMessage build(
            Transaction transaction,
            String decryptedMessage,
            boolean isISayed,
            long date,
            String companionId,
            String ownerKey
    ) {
        AdamantTransferMessage message = new AdamantTransferMessage();
        message.setSupportedType(SupportedMessageListContentType.ADAMANT_TRANSFER_MESSAGE);
        message.setiSay(isISayed);
        message.setTimestamp(date);
        message.setCompanionId(companionId);
        message.setOwnerPublicKey(ownerKey);

        if (transaction != null){
            message.setAmount(BalanceConvertHelper.convert(transaction.getAmount()));
            message.setOwnerPublicKey(transaction.getSenderPublicKey());

            message.setProcessed(true);
            message.setTransactionId(transaction.getId());
        }

        return message;
    }
}
