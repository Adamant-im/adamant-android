package im.adamant.android.ui.fragments.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import im.adamant.android.R;
import im.adamant.android.ui.presenters.SendFundsPresenter;

public class ConfirmationSendFundsDialog extends DialogFragment {
    private SendFundsPresenter presenter;
    private String message;

    public static ConfirmationSendFundsDialog provide(SendFundsPresenter presenter,  String message) {
        ConfirmationSendFundsDialog fragment = new ConfirmationSendFundsDialog();
        fragment.presenter = presenter;
        fragment.message = message;

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        return builder
                .setTitle(R.string.activity_currency_send_dialog_funds_title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    presenter.onClickConfirmSend();
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                .create();
    }
}
