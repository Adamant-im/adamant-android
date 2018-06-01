package com.dremanovich.adamant_android.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dremanovich.adamant_android.R;
import com.dremanovich.adamant_android.ui.entities.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private List<Message> messages = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.list_item_message_text);
        }
    }

    public MessagesAdapter(List<Message> messages) {
        if (messages != null){
            this.messages = messages;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_message, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (message != null){
            holder.mTextView.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void updateDataset(List<Message> messages){
        if (messages != null){
            this.messages = messages;
        } else {
            this.messages.clear();
        }

        notifyDataSetChanged();
    }
}
