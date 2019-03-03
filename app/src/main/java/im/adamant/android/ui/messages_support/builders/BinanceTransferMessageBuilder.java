package im.adamant.android.ui.messages_support.builders;

import com.google.gson.Gson;

import java.math.BigDecimal;

import im.adamant.android.core.entities.Transaction;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import im.adamant.android.ui.messages_support.entities.BinanceTransferMessage;
import im.adamant.android.ui.messages_support.entities.EthereumTransferMessage;

public class BinanceTransferMessageBuilder implements MessageBuilder<BinanceTransferMessage> {

    private static class MessageDescription {
        BigDecimal amount;
        String comments;
        String hash;
    }

    @Override
    public BinanceTransferMessage build(
            Transaction transaction,
            String decryptedMessage,
            boolean isISayed,
            long date,
            String companionId,
            String ownerKey
    ) {
        BinanceTransferMessage message = new BinanceTransferMessage();
        message.setSupportedType(SupportedMessageListContentType.BINANCE_TRANSFER);
        message.setiSay(isISayed);
        message.setTimestamp(date);
        message.setCompanionId(companionId);
        message.setOwnerPublicKey(ownerKey);

        if (transaction != null){
            message.setOwnerPublicKey(transaction.getSenderPublicKey());
            message.setStatus(AbstractMessage.Status.DELIVERED);
            message.setTransactionId(transaction.getId());
        }

        parseReachMessage(message, decryptedMessage);

        return message;
    }

    private void parseReachMessage(BinanceTransferMessage messageContainer, String decryptedMessage){
        Gson gson = new Gson();
        try {
            BinanceTransferMessageBuilder.MessageDescription description = gson.fromJson(
                    decryptedMessage,
                    BinanceTransferMessageBuilder.MessageDescription.class
            );
            messageContainer.setAmount(description.amount);
            messageContainer.setComment(description.comments);
            messageContainer.setEthereumTransactionId(description.hash);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
