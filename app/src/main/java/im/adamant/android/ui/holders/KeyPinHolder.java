package im.adamant.android.ui.holders;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import im.adamant.android.ui.adapters.KeyPinAdapter;

public abstract class KeyPinHolder extends RecyclerView.ViewHolder {
    public KeyPinHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bind(KeyPinAdapter.KeyPinEntry key);

    public interface HolderClickListener {
        void click(KeyPinAdapter.KeyPinEntry key);
    }
}
