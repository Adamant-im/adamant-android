package im.adamant.android.core.responses;

import im.adamant.android.core.entities.Account;

public class Authorization {
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
