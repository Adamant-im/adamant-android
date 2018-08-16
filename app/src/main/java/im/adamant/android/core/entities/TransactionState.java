package im.adamant.android.core.entities;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import im.adamant.android.core.encryption.Hex;

public class TransactionState implements WithBytesDigest {
    private String key;
    private String value;
    private int type;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    @Override
    public byte[] getBytesDigest() {
        byte[] keyBytes = key.getBytes();
        byte[] valueBytes = value.getBytes();

        ByteBuffer typeBuffer = ByteBuffer.allocate(INTEGER_SIZE);
        typeBuffer.order(ByteOrder.LITTLE_ENDIAN);
        typeBuffer.putInt(type);

        ByteBuffer hashBuffer = ByteBuffer.allocate(keyBytes.length + valueBytes.length + typeBuffer.position());
        hashBuffer.put(valueBytes);
        hashBuffer.put(keyBytes);
        //TODO: Don't move this line, otherwise may broke calculation buffer size process.
        typeBuffer.flip();
        hashBuffer.put(typeBuffer);

        return hashBuffer.array();
    }
}
