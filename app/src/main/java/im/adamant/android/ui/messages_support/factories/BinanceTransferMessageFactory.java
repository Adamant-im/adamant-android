package im.adamant.android.ui.messages_support.factories;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import im.adamant.android.R;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.markdown.AdamantMarkdownProcessor;
import im.adamant.android.ui.messages_support.builders.BinanceTransferMessageBuilder;
import im.adamant.android.ui.messages_support.builders.MessageBuilder;
import im.adamant.android.ui.messages_support.entities.BinanceTransferMessage;
import im.adamant.android.ui.messages_support.holders.AbstractMessageListContentViewHolder;
import im.adamant.android.ui.messages_support.holders.BinanceTransferMessageViewHolder;
import im.adamant.android.ui.messages_support.processors.MessageProcessor;

public class BinanceTransferMessageFactory implements MessageFactory<BinanceTransferMessage> {
    private AdamantMarkdownProcessor adamantAddressProcessor;
    private Avatar avatar;

    public BinanceTransferMessageFactory(AdamantMarkdownProcessor adamantAddressProcessor, Avatar avatar) {
        this.adamantAddressProcessor = adamantAddressProcessor;
        this.avatar = avatar;
    }
    @Override
    public MessageBuilder<BinanceTransferMessage> getMessageBuilder() {
        return new BinanceTransferMessageBuilder();
    }

    @Override
    public AbstractMessageListContentViewHolder getViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item_general_message, parent, false);
        return new BinanceTransferMessageViewHolder(parent.getContext(), v, adamantAddressProcessor, avatar);
    }

    @Override
    public MessageProcessor<BinanceTransferMessage> getMessageProcessor() {
        throw new Error("BinanceTransferMessageProcessor not implemented!");
    }
}
