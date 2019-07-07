package im.adamant.android.core.responses;

import im.adamant.android.core.entities.Transaction;

public class TransactionDetailsResponse {
    private int nodeTimestamp;
    private boolean success;
    private Transaction transaction;
    private String error;

    public int getNodeTimestamp() {
        return nodeTimestamp;
    }

    public TransactionDetailsResponse setNodeTimestamp(int nodeTimestamp) {
        this.nodeTimestamp = nodeTimestamp;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public TransactionDetailsResponse setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public TransactionDetailsResponse setTransaction(Transaction transaction) {
        this.transaction = transaction;
        return this;
    }

    public String getError() {
        return error;
    }
}
