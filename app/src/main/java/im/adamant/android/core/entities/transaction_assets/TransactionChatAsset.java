package im.adamant.android.core.entities.transaction_assets;

import im.adamant.android.core.entities.TransactionMessage;

public class TransactionChatAsset implements TransactionAsset {
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

    @Override
    public byte[] getBytesDigest() {
        if(chat != null){
            return chat.getBytesDigest();
        }
        return new byte[0];
    }
}
