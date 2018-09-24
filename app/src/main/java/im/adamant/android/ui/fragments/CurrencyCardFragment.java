package im.adamant.android.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import im.adamant.android.R;
import im.adamant.android.ui.adapters.CardAdapter;

public class CurrencyCardFragment extends BaseFragment {
    private CardView cardView;

    @Override
    public int getLayoutId() {
        return R.layout.list_item_currency_card;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        cardView = (CardView) view.findViewById(R.id.cardView);
        cardView.setMaxCardElevation(cardView.getCardElevation() * CardAdapter.MAX_ELEVATION_FACTOR);
        return view;
    }

    public CardView getCardView() {
        return cardView;
    }
}
