package com.dremanovich.adamant_android.ui.entities;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Random;

public class Message implements Serializable, Comparable<Message> {
    private boolean iSay;
    private String message;
    private long date;
    private boolean processed;
    private String transactionId;
    private String companionId;

    public Message() {
        //This is a temporary identifier so that messages that are not confirmed in the blockchain do not merge into one
        transactionId = "temp_" + Double.toHexString(Math.random() * 100_000);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

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

    @Override
    public int compareTo(@NonNull Message message) {
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
        Message message = (Message) o;
        return Objects.equals(transactionId, message.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }
}
