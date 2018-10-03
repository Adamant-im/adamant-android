package im.adamant.android.ui.messages_support.entities;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

import im.adamant.android.ui.messages_support.SupportedMessageType;

public abstract class AbstractMessage implements Serializable, Comparable<AbstractMessage> {
    private SupportedMessageType supportedType = SupportedMessageType.UNDEFINED;
    private boolean iSay;
    private long date;
    private boolean processed;
    private String transactionId;
    private String companionId;
    private String ownerPublicKey;
    private Bitmap avatar;

    public AbstractMessage() {
        //This is a temporary identifier so that messages that are not confirmed in the blockchain do not merge into one
        transactionId = "temp_" + Double.toHexString(Math.random() * 100_000);
    }

    public abstract String getShortedMessage(Context context, int preferredLimit);

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
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

    public SupportedMessageType getSupportedType() {
        return supportedType;
    }

    public void setSupportedType(SupportedMessageType supportedType) {
        this.supportedType = supportedType;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public String getOwnerPublicKey() {
        return ownerPublicKey;
    }

    public void setOwnerPublicKey(String ownerPublicKey) {
        this.ownerPublicKey = ownerPublicKey;
    }

    @Override
    public int compareTo(@NonNull AbstractMessage message) {
        long dateDiff = date - message.date;
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
