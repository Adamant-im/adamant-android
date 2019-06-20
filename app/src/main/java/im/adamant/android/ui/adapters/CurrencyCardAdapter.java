package im.adamant.android.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import im.adamant.android.R;
import im.adamant.android.ui.SendFundsScreen;
import im.adamant.android.ui.entities.CurrencyCardItem;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class CurrencyCardAdapter extends PagerAdapter implements CardAdapter  {

    public enum Events {
        COPY,
        CREATE_QR,
        SEND_FUNDS
    }

    private List<CardView> views;
    private List<CurrencyCardItem> items;
    private List<TabViewHolder> tabViewHolders = new ArrayList<>();
    private float mBaseElevation;
    private PublishSubject<Events> publisher = PublishSubject.create();

    public CurrencyCardAdapter() {
        items = new ArrayList<>();
        views = new ArrayList<>();
    }

    public void addCardItems(List<CurrencyCardItem> items){
        if (items != null){
            this.items = items;
            //TODO: Refactor this. Loop calls every 6 seconds
            for (int i = 0; i < views.size(); i++) {
                bind(items.get(i), views.get(i));
                if (i < tabViewHolders.size()) {
                    tabViewHolders.get(i).bind(items.get(i));
                }
            }
        }

        notifyDataSetChanged();
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return views.get(position);

    }

    public CurrencyCardItem getItem(int position) {

        return items.get(position);
    }

    public Observable<Events> getObservable(){
        return publisher;
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
                .inflate(R.layout.list_item_wallet_card, container, false);
        container.addView(view);
        bind(items.get(position), view);
        CardView cardView = (CardView) view.findViewById(R.id.list_item_wallet_card_cv_card);

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

//    @Nullable
//    @Override
//    public CharSequence getPageTitle(int position) {
//        String title = "";
//        CurrencyCardItem currencyCardItem = items.get(position);
//        if (currencyCardItem != null){
//            title = currencyCardItem.getAbbreviation();
//        }
//        return title;
//    }

    public void setSelectedIndex(int index) {
        int currentIndex = 0;
        for (TabViewHolder tabViewHolder : tabViewHolders) {
            if (currentIndex == index) {
                tabViewHolder.setSelected(true);
            } else {
                tabViewHolder.setSelected(false);
            }
            currentIndex++;
        }
    }

    public View getTabCustomView(int position, LayoutInflater inflater, ViewGroup container) {
        CurrencyCardItem item = getItem(position);

        TabViewHolder tabViewHolder = null;
        if (position < tabViewHolders.size()) {
            tabViewHolder = tabViewHolders.get(position);
        }

        if (tabViewHolder == null) {
            View customView = inflater.inflate(R.layout.wallet_tab_layout, container);
            tabViewHolder = new TabViewHolder(customView);
            tabViewHolder.bind(item);
            tabViewHolders.add(tabViewHolder);
        }

        return tabViewHolder.getCustomView();
    }

    //TODO: Maybe use ViewHolder
    private void bind(CurrencyCardItem item, View view) {
        Context ctx = view.getContext().getApplicationContext();

        ImageButton copyBtnView = view.findViewById(R.id.list_item_wallet_card_ib_copy);
        ImageButton createQrView = view.findViewById(R.id.list_item_wallet_card_ib_create_qr);

        TextView titleView = view.findViewById(R.id.list_item_wallet_card_tv_title);
        TextView balanceView = view.findViewById(R.id.list_item_wallet_card_tv_balance);
        TextView addressView = view.findViewById(R.id.list_item_wallet_card_tv_address);
        TextView airdropLinkView = view.findViewById(R.id.list_item_wallet_card_tv_get_free_token);
        TextView sendFundsLinkView = view.findViewById(R.id.list_item_wallet_card_tv_send_funds);

        String sendFundsLinkText = ctx.getString(R.string.fragment_wallet_send_funds);
        sendFundsLinkText = String.format(Locale.ENGLISH, sendFundsLinkText, item.getAbbreviation());
        sendFundsLinkView.setText(sendFundsLinkText);
        sendFundsLinkView.setOnClickListener(v -> {
            Intent intent = new Intent(ctx, SendFundsScreen.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(SendFundsScreen.ARG_WALLET_FACADE, item.getFacadeType());
            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            ctx.startActivity(intent);
        });

        ImageView backgroundLogoView = view.findViewById(R.id.list_item_wallet_card_background_logo);

        titleView.setText(item.getTitleString());
        balanceView.setText(String.format(Locale.ENGLISH, "%." + item.getPrecision() + "f", item.getBalance()));
        addressView.setText(item.getAddress());
        backgroundLogoView.setImageResource(item.getBackgroundLogoResource());

        boolean isAirdropAvailable = item.getAirdropLinkResource() > 0 || (item.getAirdropLinkString() != null && !item.getAirdropLinkString().isEmpty());

        if (isAirdropAvailable){
            String link = item.getAirdropLinkResource() > 0 ? ctx.getString(item.getAirdropLinkResource()) + item.getAddress() : item.getAirdropLinkString();
            airdropLinkView.setVisibility(View.VISIBLE);
            airdropLinkView.setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(browserIntent);
            });
        } else {
            airdropLinkView.setVisibility(View.GONE);
            airdropLinkView.setOnClickListener(null);
        }

        copyBtnView.setOnClickListener(v -> publisher.onNext(Events.COPY));
        createQrView.setOnClickListener(v -> publisher.onNext(Events.CREATE_QR));
    }


    private static class TabViewHolder {
        private static final DecimalFormat decimalFormatter = new DecimalFormat("#.###");

        private View customView;
        private ImageView iconView;
        private TextView balanceView;
        private TextView currencyView;

        public TabViewHolder(View customView) {
            this.customView = customView;

            this.iconView = customView.findViewById(R.id.wallet_tab_layout_icon);
            this.balanceView = customView.findViewById(R.id.wallet_tab_layout_balance);
            this.currencyView = customView.findViewById(R.id.wallet_tab_layout_currency);
        }

        public View getCustomView() {
            return this.customView;
        }

        public void bind(CurrencyCardItem item) {
            this.iconView.setImageResource(item.getBackgroundLogoResource());
            this.balanceView.setText(decimalFormatter.format(item.getShortedBalance()));
            this.currencyView.setText(item.getAbbreviation());
        }

        public void setSelected(boolean value) {
            Context context = currencyView.getContext();
            if (value) {
                this.iconView.setColorFilter(ContextCompat.getColor(context, R.color.secondary));
                this.balanceView.setTextColor(ContextCompat.getColor(context, R.color.secondary));
                this.currencyView.setTextColor(ContextCompat.getColor(context, R.color.secondary));
            } else {
                this.iconView.setColorFilter(ContextCompat.getColor(context, R.color.textMuted));
                this.balanceView.setTextColor(ContextCompat.getColor(context, R.color.textMuted));
                this.currencyView.setTextColor(ContextCompat.getColor(context, R.color.textMuted));
            }
        }
    }
}
