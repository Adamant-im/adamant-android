package im.adamant.android.ui.holders;

import android.view.View;

import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import im.adamant.android.R;
import im.adamant.android.ui.adapters.KeyPinAdapter;

public class KeyPinHolder extends RecyclerView.ViewHolder {
    private MaterialButton button;
    private HolderClickListener listener;

    public KeyPinHolder(@NonNull View itemView, @NonNull HolderClickListener listener) {
        super(itemView);
        this.listener = listener;
        this.button = itemView.findViewById(R.id.list_item_pincode_key_button);
    }

    public void bind(KeyPinAdapter.KeyPinEntry key) {
        if(key.getType() == KeyPinAdapter.KeyEntryType.DIGIT) {
            button.setText(key.getDigit());
        } else {
            button.setIconResource(key.getIcon());
        }

        button.setOnClickListener(v -> listener.click(key));
    }

    public interface HolderClickListener {
        void click(KeyPinAdapter.KeyPinEntry key);
    }
}
