package im.adamant.android.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import im.adamant.android.R;
import im.adamant.android.ui.entities.CurrencyTransferEntity;
import im.adamant.android.ui.holders.CurrencyTransferHolder;

public class CurrencyTransfersAdapter extends RecyclerView.Adapter<CurrencyTransferHolder> {
    private List<CurrencyTransferEntity> transfers = new ArrayList<>();

    public void refreshItems(List<CurrencyTransferEntity> transfers) {
        if (transfers != null){
            this.transfers = transfers;
        }
        notifyDataSetChanged();
    }

    public void addItemToBegin(CurrencyTransferEntity transfer) {
        this.transfers.add(0, transfer);
        notifyItemInserted(0);
    }

    public void addItemToEnd(CurrencyTransferEntity transfer) {
        this.transfers.add(transfer);
        notifyItemInserted(transfers.size());
    }

    public void clear() {
        this.transfers.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CurrencyTransferHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item_wallet_transfer, parent, false);
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
