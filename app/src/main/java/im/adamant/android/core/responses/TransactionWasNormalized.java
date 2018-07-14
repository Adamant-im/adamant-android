package im.adamant.android.core.responses;

import im.adamant.android.core.entities.Transaction;

public class TransactionWasNormalized {
    private int nodeTimestamp;
    private boolean success;
    private Transaction transaction;
    private String error;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getNodeTimestamp() {
        return nodeTimestamp;
    }

    public void setNodeTimestamp(int nodeTimestamp) {
        this.nodeTimestamp = nodeTimestamp;
    }
}
