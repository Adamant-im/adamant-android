package im.adamant.android.core.entities.transaction_assets;

import im.adamant.android.core.entities.TransactionState;

public class TransactionStateAsset implements TransactionAsset {
    private TransactionState state;

    public TransactionState getState() {
        return state;
    }

    public void setState(TransactionState state) {
        this.state = state;
    }

    @Override
    public byte[] getBytesDigest() {
        if (state == null){
            return new byte[0];
        } else {
            return state.getBytesDigest();
        }
    }
}
