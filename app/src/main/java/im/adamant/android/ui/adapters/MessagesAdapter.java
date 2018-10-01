package im.adamant.android.ui.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;


import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import im.adamant.android.ui.messages_support.holders.AbstractMessageViewHolder;
import im.adamant.android.ui.messages_support.SupportedMessageType;
import im.adamant.android.ui.messages_support.factories.MessageFactory;
import im.adamant.android.ui.messages_support.factories.MessageFactoryProvider;

import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<AbstractMessageViewHolder> {

    private List<AbstractMessage> messages = new ArrayList<>();
    private MessageFactoryProvider messageFactoryProvider;

    public MessagesAdapter(MessageFactoryProvider messageFactoryProvider, List<AbstractMessage> messages) {
        this.messageFactoryProvider = messageFactoryProvider;

        if (messages != null){
            this.messages = messages;
        }
    }

    @Override
    public int getItemViewType(int position) {
        AbstractMessage message = messages.get(position);
        return message.getSupportedType().ordinal();
    }

    @NonNull
    @Override
    public AbstractMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            MessageFactory messageFactory = messageFactoryProvider.getFactoryByType(
                    SupportedMessageType.values()[viewType]
            );

            return messageFactory.getViewHolder(parent);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public void onBindViewHolder(@NonNull AbstractMessageViewHolder holder, int position) {
        AbstractMessage message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void updateDataset(List<AbstractMessage> messages){
        if (messages != null){
            this.messages = messages;
        } else {
            this.messages.clear();
        }

        notifyDataSetChanged();
    }
}
