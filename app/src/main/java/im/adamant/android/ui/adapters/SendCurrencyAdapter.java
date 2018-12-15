package im.adamant.android.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import im.adamant.android.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import im.adamant.android.ui.entities.CurrencyCardItem;
import im.adamant.android.ui.entities.SendCurrencyEntity;


public class SendCurrencyAdapter extends PagerAdapter {

    private List<View> views;
    private List<SendCurrencyEntity> items;

    public SendCurrencyAdapter() {
        items = new ArrayList<>();
        views = new ArrayList<>();
    }

    public void setItems(List<SendCurrencyEntity> items) {
        if (items != null) {
            this.items = items;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        SendCurrencyEntity entity = items.get(position);
        if (entity != null){
            title = entity.getWalletType().name();
        }
        return title;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.list_item_currency_send_view, container, false);
        container.addView(view);
        bind(position, view);

        if (position < views.size()){
            views.set(position, view);
        } else {
            views.add(view);
        }
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
        views.set(position, null);
    }

    private void bind(int position, View view) {
        TextInputEditText addressView = view.findViewById(R.id.list_item_currency_send_address);
        TextInputEditText amountView = view.findViewById(R.id.list_item_currency_amount);
        TextInputEditText feeView = view.findViewById(R.id.list_item_currency_send_fee);

        SendCurrencyEntity entity = items.get(position);

        addressView.setText(entity.getRecipientAddress());
        amountView.setText(entity.getAmount().toString());
        feeView.setText(entity.getFee().toString());
    }
}
