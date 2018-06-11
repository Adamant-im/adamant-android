package im.adamant.android.core.requests;

import im.adamant.android.core.entities.Transaction;

public class ProcessTransaction {
    private Transaction transaction;

    public ProcessTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }
}
