package im.adamant.android.ui.entities;

import java.math.BigDecimal;

public abstract class TransferDetails {
    protected String id;
    protected BigDecimal amount;
    protected BigDecimal fee;
    protected long unixTransferDate;
    protected String fromId, toId;
    protected long confirmations;

    public static enum STATUS {
        PENDING, SUCCESS
    }

    public abstract STATUS getStatus();

    public String getId() {
        return id;
    }

    public TransferDetails setId(String id) {
        this.id = id;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransferDetails setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public TransferDetails setFee(BigDecimal fee) {
        this.fee = fee;
        return this;
    }

    public long getUnixTransferDate() {
        return unixTransferDate;
    }

    public TransferDetails setUnixTransferDate(long unixTransferDate) {
        this.unixTransferDate = unixTransferDate;
        return this;
    }

    public String getFromId() {
        return fromId;
    }

    public TransferDetails setFromId(String fromId) {
        this.fromId = fromId;
        return this;
    }

    public String getToId() {
        return toId;
    }

    public TransferDetails setToId(String toId) {
        this.toId = toId;
        return this;
    }

    public long getConfirmations() {
        return confirmations;
    }

    public TransferDetails setConfirmations(long confirmations) {
        this.confirmations = confirmations;
        return this;
    }
}
