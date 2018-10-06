package im.adamant.android.ui.messages_support.factories;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import im.adamant.android.R;
import im.adamant.android.avatars.AvatarGenerator;
import im.adamant.android.helpers.AdamantAddressProcessor;
import im.adamant.android.ui.messages_support.entities.EthereumTransferMessage;
import im.adamant.android.ui.messages_support.holders.AbstractMessageListContentViewHolder;
import im.adamant.android.ui.messages_support.holders.EthereumTransferMessageViewHolder;
import im.adamant.android.ui.messages_support.builders.EthereumTransferMessageBuilder;
import im.adamant.android.ui.messages_support.builders.MessageBuilder;
import im.adamant.android.ui.messages_support.processors.MessageProcessor;

public class EthereumTransferMessageFactory implements MessageFactory<EthereumTransferMessage> {
    private AdamantAddressProcessor adamantAddressProcessor;
    private AvatarGenerator avatarGenerator;

    public EthereumTransferMessageFactory(AdamantAddressProcessor adamantAddressProcessor, AvatarGenerator avatarGenerator) {
        this.adamantAddressProcessor = adamantAddressProcessor;
        this.avatarGenerator = avatarGenerator;
    }

    @Override
    public MessageBuilder<EthereumTransferMessage> getMessageBuilder() {
        return new EthereumTransferMessageBuilder();
    }

    @Override
    public AbstractMessageListContentViewHolder getViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item_general_message, parent, false);
        return new EthereumTransferMessageViewHolder(parent.getContext(), v, adamantAddressProcessor, avatarGenerator);
    }

    @Override
    public MessageProcessor<EthereumTransferMessage> getMessageProcessor() {
        throw new Error("EthereumTransferMessage not implemented!");
    }
}
