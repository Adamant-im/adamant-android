package com.dremanovich.adamant_android.core.entities;

import com.dremanovich.adamant_android.core.encryption.Hex;

import java.math.BigInteger;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

public class Transaction implements WithBytesDigest {
    private TransactionAsset asset;

    //TODO: maybe not a string
    private List<String> confirmations;

    private List<String> signatures;

    private String requesterPublicKey;

    private String senderId;

    private int type;

    private String id;

    private int timestamp;

    private long amount;

    private long fee;

    private int height;

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

    public int getType ()
    {
        return type;
    }

    public void setType (int type)
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

    public int getTimestamp ()
    {
        return timestamp;
    }

    public void setTimestamp (int timestamp)
    {
        this.timestamp = timestamp;
    }

    public long getAmount ()
    {
        return amount;
    }

    public void setAmount (long amount)
    {
        this.amount = amount;
    }

    public long getFee ()
    {
        return fee;
    }

    public void setFee (long fee)
    {
        this.fee = fee;
    }

    public int getHeight ()
    {
        return height;
    }

    public void setHeight (int height)
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

    public byte[] getBytesDigest(){
        int assetSize = 0;
        byte[] assetBytes = null;

        if (type == 8 && asset != null){
            assetBytes = asset.getChat().getBytesDigest();
            assetSize = assetBytes.length;
        }

        ByteBuffer bytesBuffer = ByteBuffer.allocate(1 + 4 + 32 + 8 + 8 + 64 + 64 + assetSize);
        bytesBuffer.order(ByteOrder.LITTLE_ENDIAN);

        bytesBuffer.put((byte) type);
        bytesBuffer.putInt(timestamp);
        bytesBuffer.put(Hex.encodeStringToHexArray(senderPublicKey));

        if (requesterPublicKey != null){
            bytesBuffer.put(Hex.encodeStringToHexArray(requesterPublicKey));
        }

        if (recipientId != null && !recipientId.isEmpty()){
            //TODO: Test: if recipienId more than max long value
            long recipientLongId = new BigInteger(recipientId.substring(1)).longValue();
            bytesBuffer.putLong(Long.reverseBytes(recipientLongId));

        } else {
            bytesBuffer.put(new byte[8]);
        }

        bytesBuffer.putLong(amount);

        if (assetSize > 0){
            bytesBuffer.put(assetBytes);
        }

        if (signature != null && !signature.isEmpty()){
            bytesBuffer.put(Hex.encodeStringToHexArray(signature));
        }

        int position = bytesBuffer.position();
        ByteBuffer trimmedBuffer = ByteBuffer.allocate(position);
        for (int i = 0; i < position; i++){
            trimmedBuffer.put(bytesBuffer.get(i));
        }


        return trimmedBuffer.array();
    }

}
