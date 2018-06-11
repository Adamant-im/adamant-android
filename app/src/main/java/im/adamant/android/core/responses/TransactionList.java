package im.adamant.android.core.responses;

import im.adamant.android.core.entities.Transaction;

import java.util.List;

public class TransactionList {
    private boolean success;
    private List<Transaction> transactions;
    private String error;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
