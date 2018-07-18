package im.adamant.android.ui.messages_support.factories;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import im.adamant.android.R;
import im.adamant.android.helpers.AdamantAddressProcessor;
import im.adamant.android.ui.entities.messages.AdamantBasicMessage;
import im.adamant.android.ui.holders.messages.AbstractMessageViewHolder;
import im.adamant.android.ui.holders.messages.AdamantBasicMessageViewHolder;
import im.adamant.android.ui.messages_support.builders.AdamantBasicMessageBuilder;
import im.adamant.android.ui.messages_support.builders.MessageBuilder;

public class AdamantBasicMessageFactory implements MessageFactory<AdamantBasicMessage> {
    private AdamantAddressProcessor adamantAddressProcessor;

    public AdamantBasicMessageFactory(AdamantAddressProcessor adamantAddressProcessor) {
        this.adamantAddressProcessor = adamantAddressProcessor;
    }

    @Override
    public MessageBuilder<AdamantBasicMessage> getMessageBuilder() {
        return new AdamantBasicMessageBuilder();
    }

    @Override
    public AbstractMessageViewHolder getViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item_adamant_basic_message, parent, false);
        return new AdamantBasicMessageViewHolder(parent.getContext(), v, adamantAddressProcessor);
    }
}
