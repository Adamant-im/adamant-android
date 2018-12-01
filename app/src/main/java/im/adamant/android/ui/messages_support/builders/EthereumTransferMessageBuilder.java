package im.adamant.android.ui.messages_support.builders;

import com.google.gson.Gson;

import java.math.BigDecimal;

import im.adamant.android.core.entities.Transaction;
import im.adamant.android.ui.messages_support.entities.EthereumTransferMessage;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;

public class EthereumTransferMessageBuilder implements MessageBuilder<EthereumTransferMessage> {

    private static class MessageDescription {
        BigDecimal amount;
        String comments;
        String hash;
    }

    @Override
    public EthereumTransferMessage build(
            Transaction transaction,
            String decryptedMessage,
            boolean isISayed,
            long date,
            String companionId,
            String ownerPublicKey
    ) {
        EthereumTransferMessage message = new EthereumTransferMessage();
        message.setSupportedType(SupportedMessageListContentType.ETHEREUM_TRANSFER);
        message.setiSay(isISayed);
        message.setTimestamp(date);
        message.setCompanionId(companionId);
        message.setOwnerPublicKey(ownerPublicKey);

        if (transaction != null){
            message.setOwnerPublicKey(transaction.getSenderPublicKey());
            message.setProcessed(true);
            message.setTransactionId(transaction.getId());
        }

        parseReachMessage(message, decryptedMessage);

        return message;
    }

    private void parseReachMessage(EthereumTransferMessage messageContainer, String decryptedMessage){
        Gson gson = new Gson();
        try {
            MessageDescription description = gson.fromJson(decryptedMessage, MessageDescription.class);
            messageContainer.setAmount(description.amount);
            messageContainer.setComment(description.comments);
            messageContainer.setEthereumTransactionId(description.hash);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
