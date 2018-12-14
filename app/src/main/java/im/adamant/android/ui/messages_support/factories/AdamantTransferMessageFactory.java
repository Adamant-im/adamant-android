package im.adamant.android.ui.messages_support.factories;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import im.adamant.android.R;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.helpers.AdamantAddressProcessor;
import im.adamant.android.ui.messages_support.builders.AdamantTransferMessageBuilder;
import im.adamant.android.ui.messages_support.builders.EthereumTransferMessageBuilder;
import im.adamant.android.ui.messages_support.builders.MessageBuilder;
import im.adamant.android.ui.messages_support.entities.AdamantTransferMessage;
import im.adamant.android.ui.messages_support.entities.EthereumTransferMessage;
import im.adamant.android.ui.messages_support.holders.AbstractMessageListContentViewHolder;
import im.adamant.android.ui.messages_support.holders.AdamantTransferMessageViewHolder;
import im.adamant.android.ui.messages_support.holders.EthereumTransferMessageViewHolder;
import im.adamant.android.ui.messages_support.processors.MessageProcessor;

public class AdamantTransferMessageFactory implements MessageFactory<AdamantTransferMessage> {
    private AdamantAddressProcessor adamantAddressProcessor;
    private Avatar avatar;

    public AdamantTransferMessageFactory(AdamantAddressProcessor adamantAddressProcessor, Avatar avatar) {
        this.adamantAddressProcessor = adamantAddressProcessor;
        this.avatar = avatar;
    }

    @Override
    public MessageBuilder<AdamantTransferMessage> getMessageBuilder() {
        return new AdamantTransferMessageBuilder();
    }

    @Override
    public AbstractMessageListContentViewHolder getViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item_general_message, parent, false);
        return new AdamantTransferMessageViewHolder(parent.getContext(), v, adamantAddressProcessor, avatar);
    }

    @Override
    public MessageProcessor<AdamantTransferMessage> getMessageProcessor() {
        throw new Error("AdamantTransferMessageProcessor not implemented!");
    }
}
