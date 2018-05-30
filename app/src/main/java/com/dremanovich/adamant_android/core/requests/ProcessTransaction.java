package com.dremanovich.adamant_android.core.requests;

import com.dremanovich.adamant_android.core.entities.Transaction;

public class ProcessTransaction {
    private Transaction transaction;

    public ProcessTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }
}
