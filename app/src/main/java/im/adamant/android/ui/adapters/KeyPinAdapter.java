package im.adamant.android.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import im.adamant.android.R;
import im.adamant.android.ui.holders.KeyPinDigitHolder;
import im.adamant.android.ui.holders.KeyPinHolder;
import im.adamant.android.ui.holders.KeyPinIconHolder;

public class KeyPinAdapter extends RecyclerView.Adapter<KeyPinHolder> implements KeyPinHolder.HolderClickListener {
    public static final int DIGIT_HOLDER_TYPE = 0;
    public static final int ICON_HOLDER_TYPE = 1;

    private int keyLength = 10;
    private List<KeyPinEntry> keys = new ArrayList<>();
    private PincodeListener listener = null;
    private StringBuilder pcd = new StringBuilder();

    public KeyPinAdapter() {
        initKeys();
    }

    @Override
    public int getItemViewType(int position) {
        KeyPinEntry entry = keys.get(position);
        if (entry.getType() == KeyEntryType.DIGIT) {
            return DIGIT_HOLDER_TYPE;
        } else {
            return ICON_HOLDER_TYPE;
        }
    }

    @NonNull
    @Override
    public KeyPinHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == DIGIT_HOLDER_TYPE) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_pincode_digit_key, parent, false);
            return new KeyPinDigitHolder(v, this);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_pincode_icon_key, parent, false);
            return new KeyPinIconHolder(v, this);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull KeyPinHolder holder, int position) {
        holder.bind(keys.get(position));
    }

    @Override
    public int getItemCount() {
        return keys.size();
    }

    public void shuffle() {
        initKeys();
        notifyDataSetChanged();
    }

    public void setKeyLength(int length) {
        this.keyLength = length;
    }

    public void setPincodeListener(PincodeListener listener) {
        this.listener = listener;
    }

    public void removePincodeListener() {
        this.listener = null;
    }

    protected void initKeys() {
        keys.clear();
        List<String> digits = new ArrayList<>();
        digits.add("1");
        digits.add("2");
        digits.add("3");

        digits.add("4");
        digits.add("5");
        digits.add("6");

        digits.add("7");
        digits.add("8");
        digits.add("9");

        digits.add("0");

        Collections.shuffle(digits);

        for (int i = 0; i < digits.size(); i++) {
            if (i == 9) {
                KeyPinEntry dropEntry = new KeyPinEntry(KeyEntryType.DROP, R.drawable.ic_reset_pin_code);
                keys.add(dropEntry);

                KeyPinEntry entry = new KeyPinEntry(digits.get(i), KeyEntryType.DIGIT);
                keys.add(entry);

                KeyPinEntry backspaceEntry = new KeyPinEntry(KeyEntryType.BACKSPACE, R.drawable.ic_backspace);
                keys.add(backspaceEntry);
            } else {
                KeyPinEntry entry = new KeyPinEntry(digits.get(i), KeyEntryType.DIGIT);
                keys.add(entry);
            }
        }
    }

    @Override
    public void click(KeyPinEntry key) {

    }

    public enum KeyEntryType {
        DIGIT,
        DROP,
        BACKSPACE
    }

    public static class KeyPinEntry {
        private String digit;
        private int icon;
        private KeyEntryType type;

        public KeyPinEntry(String digit, KeyEntryType type) {
            this.digit = digit;
            this.type = type;
        }

        public KeyPinEntry(KeyEntryType type, int icon) {
            this.icon = icon;
            this.type = type;
        }

        public String getDigit() {
            return digit;
        }

        public int getIcon() {
            return icon;
        }

        public KeyEntryType getType() {
            return type;
        }
    }


    public interface PincodeListener {
        void onComplete(CharSequence pincode);
        void onDropPin();
    }

}
