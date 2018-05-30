package com.dremanovich.adamant_android.core.responses;

import com.dremanovich.adamant_android.core.entities.Transaction;

public class TransactionWasNormalized {
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
}
