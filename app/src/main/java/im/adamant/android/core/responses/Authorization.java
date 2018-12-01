package im.adamant.android.core.responses;

import im.adamant.android.core.entities.Account;

public class Authorization {
    private int nodeTimestamp;
    private boolean success;
    private Account account;
    private String error;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getNodeTimestamp() {
        return nodeTimestamp;
    }

    public void setNodeTimestamp(int nodeTimestamp) {
        this.nodeTimestamp = nodeTimestamp;
    }

    @Override
    public String toString() {
        String str = "Success: " + success + ". Account: ";
        if (account == null){
            str += "null";
        } else {
            str += account.getPublicKey() + ", Balance: " + account.getBalance();
        }

        return str;
    }
}
