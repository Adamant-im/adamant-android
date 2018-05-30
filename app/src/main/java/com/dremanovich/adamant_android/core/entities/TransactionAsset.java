package com.dremanovich.adamant_android.core.entities;

public class TransactionAsset {
    private TransactionMessage chat;

    public TransactionMessage getChat ()
    {
        return chat;
    }

    public void setChat (TransactionMessage chat)
    {
        this.chat = chat;
    }

    @Override
    public String toString()
    {
        return this.getClass().getCanonicalName() + " [chat = " + chat + "]";
    }
}
