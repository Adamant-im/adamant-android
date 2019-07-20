package im.adamant.android.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import im.adamant.android.R;
import im.adamant.android.ui.entities.WelcomeCard;
import im.adamant.android.ui.holders.WelcomeCardHolder;

public class WelcomeCardsAdapter extends RecyclerView.Adapter<WelcomeCardHolder> {
    private List<WelcomeCard> cards = new ArrayList<>();

    public WelcomeCardsAdapter(List<WelcomeCard> cards) {
        if (cards != null) {
            this.cards = cards;
        }
    }

    @NonNull
    @Override
    public WelcomeCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item_welcome_card, parent, false);
        return new WelcomeCardHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull WelcomeCardHolder holder, int position) {
        holder.bind(cards.get(position));
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }
}
