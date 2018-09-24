package im.adamant.android.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;
import im.adamant.android.R;
import im.adamant.android.ui.entities.CurrencyCardItem;

public class CurrencyCardAdapter extends PagerAdapter implements CardAdapter  {

    private List<CardView> views;
    private List<CurrencyCardItem> items;
    private float mBaseElevation;

    public CurrencyCardAdapter() {
        items = new ArrayList<>();
        views = new ArrayList<>();
    }

    public void addCardItems(List<CurrencyCardItem> items){
        if (items != null){
            this.items = items;
        }

        notifyDataSetChanged();
    }

    public void addCardItem(CurrencyCardItem item) {
        views.add(null);
        items.add(item);
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return views.get(position);

    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.list_item_currency_card, container, false);
        container.addView(view);
        bind(items.get(position), view);
        CardView cardView = (CardView) view.findViewById(R.id.list_item_currency_card_cv_card);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        if (position < views.size()){
            views.set(position, cardView);
        } else {
            views.add(cardView);
        }
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        views.set(position, null);
    }

    private void bind(CurrencyCardItem item, View view) {
        TextView titleView = (TextView) view.findViewById(R.id.list_item_currency_card_tv_title);
        TextView balanceView = (TextView) view.findViewById(R.id.list_item_currency_card_tv_balance);
        TextView addressView = (TextView) view.findViewById(R.id.list_item_currency_card_tv_address);

        titleView.setText(item.getTitleString());
        balanceView.setText(String.format(Locale.ENGLISH, "%." + item.getPrecision() + "f", item.getBalance()));
        addressView.setText(item.getAddress());
    }

}
