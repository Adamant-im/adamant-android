package im.adamant.android.core.responses;

import java.util.List;

import im.adamant.android.core.entities.HasNodeTimestamp;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.transaction_assets.TransactionAsset;

public class MessageList implements HasNodeTimestamp {
    private boolean success;
    private int nodeTimestamp;
    private int count;
    private List<Transaction<? super TransactionAsset>> messages;
    private String error;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getNodeTimestamp() {
        return nodeTimestamp;
    }

    public void setNodeTimestamp(int nodeTimestamp) {
        this.nodeTimestamp = nodeTimestamp;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Transaction<? super TransactionAsset>> getMessages() {
        return messages;
    }

    public void setMessages(List<Transaction<? super TransactionAsset>> messages) {
        this.messages = messages;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
