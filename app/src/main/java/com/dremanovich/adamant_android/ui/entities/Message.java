package com.dremanovich.adamant_android.ui.entities;

import java.io.Serializable;

public class Message implements Serializable {
    private boolean iSay;
    private String message;
    private String date;
    private boolean processed;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
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
}
