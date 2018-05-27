package com.dremanovich.adamant_android.core.entities;

import java.util.List;

public class Transaction {
    private TransactionAsset asset;

    //TODO: maybe not a string
    private List<String> confirmations;

    private List<String> signatures;

    private String requesterPublicKey;

    private String senderId;

    private String type;

    private String id;

    private String timestamp;

    private String amount;

    private String fee;

    private String height;

    private String recipientId;

    private String signSignature;

    private String blockId;

    private String recipientPublicKey;

    private String senderPublicKey;

    private String signature;

    public TransactionAsset getAsset ()
    {
        return asset;
    }

    public void setAsset (TransactionAsset asset)
    {
        this.asset = asset;
    }

    public List<String> getConfirmations ()
    {
        return confirmations;
    }

    public void setConfirmations (List<String> confirmations)
    {
        this.confirmations = confirmations;
    }

    public List<String> getSignatures ()
    {
        return signatures;
    }

    public void setSignatures (List<String> signatures)
    {
        this.signatures = signatures;
    }

    public String getRequesterPublicKey ()
    {
        return requesterPublicKey;
    }

    public void setRequesterPublicKey (String requesterPublicKey)
    {
        this.requesterPublicKey = requesterPublicKey;
    }

    public String getSenderId ()
    {
        return senderId;
    }

    public void setSenderId (String senderId)
    {
        this.senderId = senderId;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getTimestamp ()
    {
        return timestamp;
    }

    public void setTimestamp (String timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getAmount ()
    {
        return amount;
    }

    public void setAmount (String amount)
    {
        this.amount = amount;
    }

    public String getFee ()
    {
        return fee;
    }

    public void setFee (String fee)
    {
        this.fee = fee;
    }

    public String getHeight ()
    {
        return height;
    }

    public void setHeight (String height)
    {
        this.height = height;
    }

    public String getRecipientId ()
    {
        return recipientId;
    }

    public void setRecipientId (String recipientId)
    {
        this.recipientId = recipientId;
    }

    public String getSignSignature ()
    {
        return signSignature;
    }

    public void setSignSignature (String signSignature)
    {
        this.signSignature = signSignature;
    }

    public String getBlockId ()
    {
        return blockId;
    }

    public void setBlockId (String blockId)
    {
        this.blockId = blockId;
    }

    public String getRecipientPublicKey ()
    {
        return recipientPublicKey;
    }

    public void setRecipientPublicKey (String recipientPublicKey)
    {
        this.recipientPublicKey = recipientPublicKey;
    }

    public String getSenderPublicKey ()
    {
        return senderPublicKey;
    }

    public void setSenderPublicKey (String senderPublicKey)
    {
        this.senderPublicKey = senderPublicKey;
    }

    public String getSignature ()
    {
        return signature;
    }

    public void setSignature (String signature)
    {
        this.signature = signature;
    }

    @Override
    public String toString()
    {
        return  this.getClass().getCanonicalName() + " [asset = " + asset + ", confirmations = " + confirmations + ", signatures = " + signatures + ", requesterPublicKey = " + requesterPublicKey + ", senderId = " + senderId + ", type = " + type + ", id = " + id + ", timestamp = " + timestamp + ", amount = " + amount + ", fee = " + fee + ", height = " + height + ", recipientId = " + recipientId + ", signSignature = " + signSignature + ", blockId = " + blockId + ", recipientPublicKey = " + recipientPublicKey + ", senderPublicKey = " + senderPublicKey + ", signature = " + signature + "]";
    }
}
