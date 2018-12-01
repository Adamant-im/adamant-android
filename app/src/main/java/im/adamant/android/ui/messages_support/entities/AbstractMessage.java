package im.adamant.android.ui.messages_support.entities;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import im.adamant.android.ui.messages_support.SupportedMessageListContentType;

public abstract class AbstractMessage implements MessageListContent, Serializable, Comparable<AbstractMessage> {
    private SupportedMessageListContentType supportedType = SupportedMessageListContentType.UNDEFINED;
    private boolean iSay;
    private long timestamp;
    private boolean processed;
    private String transactionId;
    private String companionId;
    private String ownerPublicKey;

    private Date date;

    public AbstractMessage() {
        //This is a temporary identifier so that messages that are not confirmed in the blockchain do not merge into one
        transactionId = "temp_" + Double.toHexString(Math.random() * 100_000);
    }

    public abstract String getShortedMessage(Context context, int preferredLimit);

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public boolean isiSay() {
        return iSay;
    }

    public void setiSay(boolean iSay) {
        this.iSay = iSay;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getCompanionId() {
        return companionId;
    }

    public void setCompanionId(String companionId) {
        this.companionId = companionId;
    }

    @Override
    public SupportedMessageListContentType getSupportedType() {
        return supportedType;
    }

    @Override
    public void setSupportedType(SupportedMessageListContentType supportedType) {
        this.supportedType = supportedType;
    }

    public String getOwnerPublicKey() {
        return ownerPublicKey;
    }

    public void setOwnerPublicKey(String ownerPublicKey) {
        this.ownerPublicKey = ownerPublicKey;
    }

    public Date getDate() {
        if (date == null){
            date = new Date(timestamp);
        }
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public int compareTo(@NonNull AbstractMessage message) {
        long dateDiff = timestamp - message.timestamp;
        if((dateDiff) > 0) {
            return 1;
        } else if (dateDiff == 0){
            return 0;
        } else {
            return -1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractMessage message = (AbstractMessage) o;
        return Objects.equals(transactionId, message.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }

    protected String shorteningString(String value, int limit){
        final String dots = "...";

        if (value == null){
            return "";
        }

        int newLinePosition = value.indexOf('\r');

        String shortString = null;
        if (newLinePosition > 0){
            shortString = value.substring(0, newLinePosition - 1);
        } else {
            shortString = value;
        }

        if (shortString.length() > (limit + dots.length())) {
            shortString = shortString.substring(0, limit) + dots;
        }

        return shortString;
    }
}
