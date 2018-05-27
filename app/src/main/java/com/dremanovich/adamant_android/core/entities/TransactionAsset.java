package com.dremanovich.adamant_android.core.entities;

public class TransactionAsset {
    private TransactionChat chat;

    public TransactionChat getChat ()
    {
        return chat;
    }

    public void setChat (TransactionChat chat)
    {
        this.chat = chat;
    }

    @Override
    public String toString()
    {
        return this.getClass().getCanonicalName() + " [chat = " + chat + "]";
    }
}
