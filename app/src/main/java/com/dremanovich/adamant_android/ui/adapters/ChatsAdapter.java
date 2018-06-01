package com.dremanovich.adamant_android.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dremanovich.adamant_android.R;
import com.dremanovich.adamant_android.ui.entities.Chat;

import java.util.ArrayList;
import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder>{
    private List<Chat> chats = new ArrayList<>();
    private SelectItemListener listener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTextView;
        public ViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.list_item_chat_name);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(listener != null) {
                listener.itemWasSelected(chats.get(getAdapterPosition()));
            }
        }
    }

    public interface SelectItemListener {
        void itemWasSelected(Chat chat);
    }

    public ChatsAdapter(List<Chat> chats, SelectItemListener listener) {
        if (chats != null){
            this.chats = chats;
        }

        if (listener != null){
            this.listener = listener;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_chat, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Chat chat = chats.get(position);
        if (chat != null){
            holder.mTextView.setText(chat.getCompanionId());
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public void updateDataset(List<Chat> chats){
        if (chats != null){
            this.chats = chats;
        } else {
            this.chats.clear();
        }

        notifyDataSetChanged();
    }

}
