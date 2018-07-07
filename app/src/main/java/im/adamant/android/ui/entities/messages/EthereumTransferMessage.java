package im.adamant.android.ui.entities.messages;

import android.content.Context;

import java.math.BigDecimal;

public class EthereumTransferMessage extends AbstractMessage {
    private BigDecimal amount;
    private String comment;
    private String ethereumTransactionId;

    @Override
    public String getShortedMessage(Context context, int preferredLimit) {
        return shorteningString(comment, preferredLimit);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getEthereumTransactionId() {
        return ethereumTransactionId;
    }

    public void setEthereumTransactionId(String ethereumTransactionId) {
        this.ethereumTransactionId = ethereumTransactionId;
    }
}
