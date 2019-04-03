package im.adamant.android.ui.holders;

import android.content.res.ColorStateList;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import im.adamant.android.R;
import im.adamant.android.ui.adapters.KeyPinAdapter;

public class KeyPinDigitHolder extends KeyPinHolder {
//    private Button button;
    private CardView cardLayout;
    private TextView textLayout;
    private HolderClickListener listener;
    private ColorStateList iconColors;

    public KeyPinDigitHolder(@NonNull View itemView, @NonNull HolderClickListener listener) {
        super(itemView);
        this.listener = listener;

        this.cardLayout = itemView.findViewById(R.id.list_item_pincode_key_button);
        this.textLayout = itemView.findViewById(R.id.list_item_pincode_key_text);
    }

    public void bind(KeyPinAdapter.KeyPinEntry key) {
        if(key.getType() == KeyPinAdapter.KeyEntryType.DIGIT) {
            textLayout.setText(key.getDigit());
        } else {
//            button.setIconResource(key.getIcon());
//            button.setIconTint(iconColors);
            cardLayout.setBackgroundResource(key.getIcon());
        }

        cardLayout.setOnClickListener(v -> {
            listener.click(key);
        });
    }
}
