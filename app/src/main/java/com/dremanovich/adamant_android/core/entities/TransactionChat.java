package com.dremanovich.adamant_android.core.entities;

public class TransactionChat {
    private String message;

    private String type;

    private String own_message;

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public String getOwn_message ()
    {
        return own_message;
    }

    public void setOwn_message (String own_message)
    {
        this.own_message = own_message;
    }

    @Override
    public String toString()
    {
        return this.getClass().getCanonicalName() + " [message = " + message + ", type = " + type + ", own_message = " + own_message + "]";
    }
}
