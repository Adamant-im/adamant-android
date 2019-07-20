package im.adamant.android.ui.holders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.curioustechizen.ago.RelativeTimeTextView;

import java.util.Locale;

import im.adamant.android.R;
import im.adamant.android.ui.entities.CurrencyTransferEntity;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

public class CurrencyTransferHolder extends RecyclerView.ViewHolder {
    public static interface OnClicked{
        void onClicked(int adapterPosition);
    }

    private Context context;

    private TextView addressView;
    private TextView amountView;
    private TextView titleView;
    private RelativeTimeTextView dateView;
    private OnClicked onClickedListener;

    public void setOnClickedListener(OnClicked onClickedListener) {
        this.onClickedListener = onClickedListener;
    }

    public CurrencyTransferHolder(@NonNull View itemView, Context context) {
        super(itemView);

        itemView.setOnClickListener(v->{
            int position = getAdapterPosition();
            if(position!=NO_POSITION && onClickedListener != null){
                    onClickedListener.onClicked(position);
            }
        });

        this.context = context;

        addressView = itemView.findViewById(R.id.list_item_wallet_transfer_tv_address);
        amountView = itemView.findViewById(R.id.list_item_wallet_transfer_tv_amount);
        titleView = itemView.findViewById(R.id.list_item_wallet_transfer_tv_title);
        dateView = itemView.findViewById(R.id.list_item_wallet_transfer_rtv_date);
    }

    public void bind(CurrencyTransferEntity transferEntity) {

        boolean hideAddress = (transferEntity.getAddress() == null) ||
                (transferEntity.getContactName() == null) ||
                (
                        (transferEntity.getAddress().equalsIgnoreCase(transferEntity.getContactName())) ||
                        transferEntity.getContactName().isEmpty()
                );

        if (hideAddress) {
            addressView.setVisibility(View.GONE);
        } else {
            addressView.setVisibility(View.VISIBLE);
            addressView.setText(transferEntity.getAddress());
        }

        if (transferEntity.getContactName().isEmpty()){
            titleView.setText(transferEntity.getAddress());
        } else {
            titleView.setText(transferEntity.getContactName());
        }

        String amountString = "";
        int amountColor = 0;
        if (transferEntity.getDirection() == CurrencyTransferEntity.Direction.SEND){
            amountString = "-";
            amountColor = ContextCompat.getColor(context, R.color.colorWarning);
        } else {
            amountColor = ContextCompat.getColor(context, R.color.colorSuccess);
            amountString = "+";
        }

        amountString += String.format(
                Locale.ENGLISH,
                "%." + transferEntity.getPrecision() + "f",
                transferEntity.getAmount()
        );

        amountView.setText(amountString);
        amountView.setTextColor(amountColor);

        dateView.setReferenceTime(transferEntity.getUnixTransferDate());
    }
}
