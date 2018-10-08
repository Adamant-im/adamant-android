package im.adamant.android.ui.messages_support.factories;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import im.adamant.android.R;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.Encryptor;
import im.adamant.android.helpers.AdamantAddressProcessor;
import im.adamant.android.ui.messages_support.entities.AdamantBasicMessage;
import im.adamant.android.ui.messages_support.holders.AbstractMessageListContentViewHolder;
import im.adamant.android.ui.messages_support.holders.AdamantBasicMessageViewHolder;
import im.adamant.android.ui.messages_support.builders.AdamantBasicMessageBuilder;
import im.adamant.android.ui.messages_support.builders.MessageBuilder;
import im.adamant.android.ui.messages_support.processors.AdamantBasicMessageProcessor;
import im.adamant.android.ui.messages_support.processors.MessageProcessor;

public class AdamantBasicMessageFactory implements MessageFactory<AdamantBasicMessage> {
    private AdamantAddressProcessor adamantAddressProcessor;
    private Encryptor encryptor;
    private AdamantApiWrapper api;
    private Avatar avatar;

    public AdamantBasicMessageFactory(
            AdamantAddressProcessor adamantAddressProcessor,
            Encryptor encryptor,
            AdamantApiWrapper api,
            Avatar avatar
    ) {
        this.adamantAddressProcessor = adamantAddressProcessor;
        this.avatar = avatar;
        this.encryptor = encryptor;
        this.api = api;
    }

    @Override
    public MessageBuilder<AdamantBasicMessage> getMessageBuilder() {
        return new AdamantBasicMessageBuilder();
    }

    @Override
    public AbstractMessageListContentViewHolder getViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item_general_message, parent, false);
        return new AdamantBasicMessageViewHolder(parent.getContext(), v, adamantAddressProcessor, avatar);
    }

    @Override
    public MessageProcessor<AdamantBasicMessage> getMessageProcessor() {
        return new AdamantBasicMessageProcessor(encryptor, api);
    }
}
