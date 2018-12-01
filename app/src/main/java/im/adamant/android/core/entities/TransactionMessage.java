package im.adamant.android.core.entities;

import im.adamant.android.core.encryption.Hex;
import com.google.gson.annotations.SerializedName;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TransactionMessage implements WithBytesDigest {
    public static final int OLD_BASE_MESSAGE_TYPE = 0;
    public static final int BASE_MESSAGE_TYPE = 1;
    public static final int RICH_MESSAGE_TYPE = 2;
    public static final int SIGNAL_MESSAGE_TYPE = 3;

    private String message;

    private int type;

    @SerializedName("own_message")
    private String ownMessage;

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public int getType ()
    {
        return type;
    }

    public void setType (int type)
    {
        this.type = type;
    }

    public String getOwnMessage()
    {
        return ownMessage;
    }

    public void setOwnMessage(String ownMessage)
    {
        this.ownMessage = ownMessage;
    }

    @Override
    public String toString()
    {
        return this.getClass().getCanonicalName() + " [message = " + message + ", type = " + type + ", ownMessage = " + ownMessage + "]";
    }

    @Override
    public byte[] getBytesDigest() {
        byte[] messageBytes = Hex.encodeStringToHexArray(message);
        byte[] ownMessageBytes = Hex.encodeStringToHexArray(ownMessage);

        ByteBuffer typeBuffer = ByteBuffer.allocate(INTEGER_SIZE);
        typeBuffer.order(ByteOrder.LITTLE_ENDIAN);
        typeBuffer.putInt(type);

        ByteBuffer hashBuffer = ByteBuffer.allocate(messageBytes.length + ownMessageBytes.length + typeBuffer.position());
        hashBuffer.put(messageBytes);
        hashBuffer.put(ownMessageBytes);
        typeBuffer.flip();
        hashBuffer.put(typeBuffer);

        return hashBuffer.array();
    }
}
