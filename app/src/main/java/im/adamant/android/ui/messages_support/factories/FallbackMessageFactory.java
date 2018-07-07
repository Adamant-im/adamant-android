package im.adamant.android.ui.messages_support.factories;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import im.adamant.android.R;
import im.adamant.android.ui.entities.messages.FallbackMessage;
import im.adamant.android.ui.holders.messages.AbstractMessageViewHolder;
import im.adamant.android.ui.holders.messages.FallbackMessageViewHolder;
import im.adamant.android.ui.messages_support.builders.FallbackMessageBuilder;
import im.adamant.android.ui.messages_support.builders.MessageBuilder;

public class FallbackMessageFactory implements MessageFactory<FallbackMessage> {
    @Override
    public MessageBuilder<FallbackMessage> getMessageBuilder() {
        return new FallbackMessageBuilder();
    }

    @Override
    public AbstractMessageViewHolder getViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item_adamant_basic_message, parent, false);
        return new FallbackMessageViewHolder(parent.getContext(), v);
    }
}
