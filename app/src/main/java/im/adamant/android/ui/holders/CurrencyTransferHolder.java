package im.adamant.android.ui.holders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import im.adamant.android.R;
import im.adamant.android.currencies.CurrencyTransferEntity;

public class CurrencyTransferHolder extends RecyclerView.ViewHolder {
    private Context context;

    private TextView addressView;
    private TextView amountView;
    private TextView titleView;

    public CurrencyTransferHolder(@NonNull View itemView, Context context) {
        super(itemView);

        this.context = context;

        addressView = itemView.findViewById(R.id.list_item_currency_transfer_tv_address);
        amountView = itemView.findViewById(R.id.list_item_currency_transfer_tv_amount);
        titleView = itemView.findViewById(R.id.list_item_currency_transfer_tv_title);
    }

    public void bind(CurrencyTransferEntity transferEntity) {

        boolean hideAddress = (transferEntity.getAddress().equalsIgnoreCase(transferEntity.getContactName())) ||
                transferEntity.getContactName().isEmpty();

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
    }
}
