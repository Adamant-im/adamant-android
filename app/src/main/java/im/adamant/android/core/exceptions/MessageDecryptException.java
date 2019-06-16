package im.adamant.android.core.exceptions;

public class MessageDecryptException extends Exception {
    private String companionId;
    private String transactionId;
    private long timestamp;
    private boolean isISay;

    public MessageDecryptException(Throwable cause, String companionId, String transactionId, boolean isISay, long timestamp) {
        super(cause);
        this.companionId = companionId;
        this.timestamp = timestamp;
        this.isISay = isISay;
        this.transactionId = transactionId;
    }

    public MessageDecryptException(String message, String companionId, String transactionId, boolean isISay, long timestamp) {
        super(message);
        this.companionId = companionId;
        this.timestamp = timestamp;
        this.transactionId = transactionId;
        this.isISay = isISay;
    }

    public String getCompanionId() {
        return companionId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isISay() {
        return isISay;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
