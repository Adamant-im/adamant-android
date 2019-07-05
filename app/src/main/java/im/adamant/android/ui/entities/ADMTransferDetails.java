package im.adamant.android.ui.entities;

import im.adamant.android.interactors.wallets.TransferDetails;

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
