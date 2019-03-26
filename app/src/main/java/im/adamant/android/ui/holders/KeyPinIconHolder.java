package im.adamant.android.ui.holders;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import im.adamant.android.R;
import im.adamant.android.ui.adapters.KeyPinAdapter;

public class KeyPinIconHolder extends KeyPinHolder {
    private Context context;
    private ImageButton button;
    private HolderClickListener listener;

    public KeyPinIconHolder(@NonNull View itemView, @NonNull HolderClickListener listener) {
        super(itemView);
        this.listener = listener;
        this.context = itemView.getContext();
        this.button = itemView.findViewById(R.id.list_item_pincode_icon_button);
    }

    public void bind(KeyPinAdapter.KeyPinEntry key) {
        if(key.getType() != KeyPinAdapter.KeyEntryType.DIGIT) {
            button.setImageDrawable(AppCompatResources.getDrawable(context, key.getIcon()));
        }

        button.setOnClickListener(v -> {
            listener.click(key);
        });
    }
}
