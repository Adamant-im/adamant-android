package im.adamant.android.core.entities;

public class Account {
    private String address;
    private long balance;
    private long uncofirmedBalance;
    private String publicKey;

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public long getUncofirmedBalance() {
        return uncofirmedBalance;
    }

    public void setUncofirmedBalance(long uncofirmedBalance) {
        this.uncofirmedBalance = uncofirmedBalance;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getAddress() {
        return address;
    }

    public String getPurifiedAddress() {
        return address.substring(1);
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
