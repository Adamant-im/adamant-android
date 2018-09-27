package im.adamant.android.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import im.adamant.android.R;
import im.adamant.android.currencies.CurrencyTransferEntity;
import im.adamant.android.ui.holders.CurrencyTransferHolder;

public class CurrencyTransfersAdapter extends RecyclerView.Adapter<CurrencyTransferHolder> {
    private List<CurrencyTransferEntity> transfers = new ArrayList<>();

    public void refreshItems(List<CurrencyTransferEntity> transfers) {
        if (transfers != null){
            this.transfers = transfers;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CurrencyTransferHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item_currency_transfer, parent, false);
        return new CurrencyTransferHolder(v, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull CurrencyTransferHolder holder, int position) {
        CurrencyTransferEntity item = transfers.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return transfers.size();
    }
}
