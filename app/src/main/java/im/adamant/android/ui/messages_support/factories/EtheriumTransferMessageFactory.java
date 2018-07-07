package im.adamant.android.ui.messages_support.factories;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import im.adamant.android.R;
import im.adamant.android.ui.entities.messages.EthereumTransferMessage;
import im.adamant.android.ui.holders.messages.AbstractMessageViewHolder;
import im.adamant.android.ui.holders.messages.EtheriumTransferMessageViewHolder;
import im.adamant.android.ui.messages_support.builders.EthereumTransferMessageBuilder;
import im.adamant.android.ui.messages_support.builders.MessageBuilder;

public class EtheriumTransferMessageFactory implements MessageFactory<EthereumTransferMessage> {
    @Override
    public MessageBuilder<EthereumTransferMessage> getMessageBuilder() {
        return new EthereumTransferMessageBuilder();
    }

    @Override
    public AbstractMessageViewHolder getViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item_adamant_basic_message, parent, false);
        return new EtheriumTransferMessageViewHolder(parent.getContext(), v);
    }
}
