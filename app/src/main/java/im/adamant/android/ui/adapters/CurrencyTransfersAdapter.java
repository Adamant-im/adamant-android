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
    private OnTransferClickedLister onClickedLister;

    public static interface OnTransferClickedLister {
        public void onTransferClicked(CurrencyTransferEntity currencyTransferEntity);
    }

    public void setOnClickedLister(OnTransferClickedLister onClickedLister) {
        this.onClickedLister = onClickedLister;
    }

    public void refreshItems(List<CurrencyTransferEntity> transfers) {
        if (transfers != null) {
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

    private CurrencyTransferHolder.OnClicked holderOnClick = new CurrencyTransferHolder.OnClicked() {
        @Override
        public void onClicked(int adapterPosition) {
            if (adapterPosition >= 0 && adapterPosition < transfers.size()) {
                CurrencyTransferEntity currencyTransferEntity = transfers.get(adapterPosition);
                if (onClickedLister != null) {
                    onClickedLister.onTransferClicked(currencyTransferEntity);
                }
            }
        }
    };

    @NonNull
    @Override
    public CurrencyTransferHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item_wallet_transfer, parent, false);
        CurrencyTransferHolder holder = new CurrencyTransferHolder(v, parent.getContext());
        if (onClickedLister != null) {
            holder.setOnClickedListener(holderOnClick);
        }
        return holder;
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
