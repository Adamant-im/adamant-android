package im.adamant.android.core.responses;

import java.util.List;

import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.transaction_assets.TransactionAsset;

public class ParametrizedTransactionList<Asset extends TransactionAsset> {
    private int nodeTimestamp;
    private boolean success;
    private List<Transaction<Asset>> transactions;
    private String error;
    private int count;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Transaction<Asset>> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction<Asset>> transactions) {
        this.transactions = transactions;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
