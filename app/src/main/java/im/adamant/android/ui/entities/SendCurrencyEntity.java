package im.adamant.android.ui.entities;

import java.math.BigDecimal;

import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;

public class SendCurrencyEntity {

    public enum Warnings {
        NOT_ENOUGH_MONEY,
        NO_WARNINGS
    }

    private SupportedWalletFacadeType walletType;
    private int currencyIconResource;
    private String recipientAddress;
    private BigDecimal currentBalance = BigDecimal.ZERO;
    private BigDecimal amount = BigDecimal.ZERO;
    private BigDecimal fee = BigDecimal.ZERO;
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private BigDecimal reminder = BigDecimal.ZERO;
    private String comment;
    private boolean isSupportComment;
    private boolean isNotSupportedYet;

    public SupportedWalletFacadeType getWalletType() {
        return walletType;
    }

    public void setWalletType(SupportedWalletFacadeType walletType) {
        this.walletType = walletType;
    }

    public int getCurrencyIconResource() {
        return currencyIconResource;
    }

    public void setCurrencyIconResource(int currencyIconResource) {
        this.currencyIconResource = currencyIconResource;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getReminder() {
        return reminder;
    }

    public void setReminder(BigDecimal reminder) {
        this.reminder = reminder;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isSupportComment() {
        return isSupportComment;
    }

    public void setSupportComment(boolean supportComment) {
        isSupportComment = supportComment;
    }

    public boolean isNotSupportedYet() {
        return isNotSupportedYet;
    }

    public void setNotSupportedYet(boolean notSupportedYet) {
        isNotSupportedYet = notSupportedYet;
    }
}
