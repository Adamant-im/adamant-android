package com.dremanovich.adamant_android.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dremanovich.adamant_android.R;
import com.dremanovich.adamant_android.ui.entities.Message;
import com.dremanovich.adamant_android.ui.holders.ReceivedMessageHolder;
import com.dremanovich.adamant_android.ui.holders.SendedMessageHolder;
import com.github.curioustechizen.ago.RelativeTimeTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int SENDED_MESSAGE_HOLDER_TYPE = 1;
    private static final int RECEIVED_MESSAGE_HOLDER_TYPE = 2;

    private List<Message> messages = new ArrayList<>();

    public MessagesAdapter(List<Message> messages) {
        if (messages != null){
            this.messages = messages;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);

        if (message.isiSay()){
            return SENDED_MESSAGE_HOLDER_TYPE;
        } else {
            return RECEIVED_MESSAGE_HOLDER_TYPE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case SENDED_MESSAGE_HOLDER_TYPE : {
                View v = inflater.inflate(R.layout.list_item_message_sended, parent, false);
                return new SendedMessageHolder(v);
            }
            case RECEIVED_MESSAGE_HOLDER_TYPE : {
                View v = inflater.inflate(R.layout.list_item_message_received, parent, false);
                return new ReceivedMessageHolder(v);
            }
            default: {
                return null;
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        switch (holder.getItemViewType()) {
            case SENDED_MESSAGE_HOLDER_TYPE:
                ((SendedMessageHolder) holder).bind(message);
                break;
            case RECEIVED_MESSAGE_HOLDER_TYPE:
                ((ReceivedMessageHolder) holder).bind(message);
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
