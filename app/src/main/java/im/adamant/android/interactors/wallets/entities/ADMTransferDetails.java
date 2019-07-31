package im.adamant.android.interactors.wallets.entities;

public class ADMTransferDetails extends TransferDetails {
    @Override
    public STATUS getStatus() {
        if (getConfirmations() > 0) {
            return STATUS.SUCCESS;
        } else {
            return STATUS.PENDING;
        }
    }
}
