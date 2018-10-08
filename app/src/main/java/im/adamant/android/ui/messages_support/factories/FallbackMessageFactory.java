package im.adamant.android.ui.messages_support.factories;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import im.adamant.android.R;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.helpers.AdamantAddressProcessor;
import im.adamant.android.ui.messages_support.entities.FallbackMessage;
import im.adamant.android.ui.messages_support.holders.AbstractMessageListContentViewHolder;
import im.adamant.android.ui.messages_support.holders.FallbackMessageViewHolder;
import im.adamant.android.ui.messages_support.builders.FallbackMessageBuilder;
import im.adamant.android.ui.messages_support.builders.MessageBuilder;
import im.adamant.android.ui.messages_support.processors.MessageProcessor;

public class FallbackMessageFactory implements MessageFactory<FallbackMessage> {
    private AdamantAddressProcessor adamantAddressProcessor;
    private Avatar avatar;

    public FallbackMessageFactory(AdamantAddressProcessor adamantAddressProcessor, Avatar avatar) {
        this.adamantAddressProcessor = adamantAddressProcessor;
        this.avatar = avatar;
    }

    @Override
    public MessageBuilder<FallbackMessage> getMessageBuilder() {
        return new FallbackMessageBuilder();
    }

    @Override
    public AbstractMessageListContentViewHolder getViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item_general_message, parent, false);
        return new FallbackMessageViewHolder(parent.getContext(), v, adamantAddressProcessor, avatar);
    }

    //No need implementation
    @Override
    public MessageProcessor<FallbackMessage> getMessageProcessor() {
        throw new Error("FallbackMessageProcessor not implemented!");
    }
}
