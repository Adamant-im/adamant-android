package im.adamant.android.ui.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import im.adamant.android.R;
import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import im.adamant.android.ui.messages_support.entities.MessageListContent;
import im.adamant.android.ui.messages_support.holders.AbstractMessageListContentViewHolder;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.factories.MessageFactory;
import im.adamant.android.ui.messages_support.factories.MessageFactoryProvider;
import im.adamant.android.ui.messages_support.holders.SeparatorViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<AbstractMessageListContentViewHolder> {

    private List<MessageListContent> messages = new ArrayList<>();
    private MessageFactoryProvider messageFactoryProvider;

    public MessagesAdapter(MessageFactoryProvider messageFactoryProvider, List<MessageListContent> messages) {
        this.messageFactoryProvider = messageFactoryProvider;

        if (messages != null){
            this.messages = messages;
        }
    }

    @Override
    public int getItemViewType(int position) {
        MessageListContent message = messages.get(position);
        return message.getSupportedType().ordinal();
    }

    @NonNull
    @Override
    public AbstractMessageListContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            int separatorType = SupportedMessageListContentType.SEPARATOR.ordinal();
            if (viewType == separatorType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View v = inflater.inflate(R.layout.list_item_separator_message, parent, false);
                return new SeparatorViewHolder(parent.getContext(), v);
            } else {
                MessageFactory messageFactory = messageFactoryProvider.getFactoryByType(
                        SupportedMessageListContentType.values()[viewType]
                );

                return messageFactory.getViewHolder(parent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public void onBindViewHolder(@NonNull AbstractMessageListContentViewHolder holder, int position) {
        MessageListContent message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void updateDataset(List<MessageListContent> messages){
        if (messages != null){
            this.messages = messages;
        } else {
            this.messages.clear();
        }

        notifyDataSetChanged();
    }
}
