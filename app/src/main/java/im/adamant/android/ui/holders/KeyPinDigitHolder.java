package im.adamant.android.ui.holders;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

import im.adamant.android.R;
import im.adamant.android.ui.adapters.KeyPinAdapter;

public class KeyPinDigitHolder extends KeyPinHolder {
    private Context ctx;
    private MaterialButton button;
    private HolderClickListener listener;

    public KeyPinDigitHolder(@NonNull View itemView, @NonNull HolderClickListener listener) {
        super(itemView);
        this.ctx = itemView.getContext();
        this.listener = listener;

        this.button = itemView.findViewById(R.id.list_item_pincode_key_button);
    }

    public void bind(KeyPinAdapter.KeyPinEntry key) {
        if(key.getType() == KeyPinAdapter.KeyEntryType.DIGIT) {
            button.setText(key.getDigit());
        } else {
            button.setIcon(ContextCompat.getDrawable(ctx, key.getIcon()));
        }

        button.setOnClickListener(v -> {
            listener.click(key);
        });
    }
}
