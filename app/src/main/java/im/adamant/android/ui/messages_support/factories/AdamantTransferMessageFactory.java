package im.adamant.android.ui.messages_support.factories;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import im.adamant.android.R;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.Encryptor;
import im.adamant.android.markdown.AdamantMarkdownProcessor;
import im.adamant.android.helpers.PublicKeyStorage;
import im.adamant.android.ui.messages_support.builders.AdamantTransferMessageBuilder;
import im.adamant.android.ui.messages_support.builders.MessageBuilder;
import im.adamant.android.ui.messages_support.entities.AdamantTransferMessage;
import im.adamant.android.ui.messages_support.holders.AbstractMessageListContentViewHolder;
import im.adamant.android.ui.messages_support.holders.AdamantTransferMessageViewHolder;
import im.adamant.android.ui.messages_support.processors.AdamantTransferMessageProcessor;
import im.adamant.android.ui.messages_support.processors.MessageProcessor;

public class AdamantTransferMessageFactory implements MessageFactory<AdamantTransferMessage> {
    private AdamantMarkdownProcessor adamantAddressProcessor;
    private Encryptor encryptor;
    private AdamantApiWrapper api;
    private Avatar avatar;
    private PublicKeyStorage publicKeyStorage;

    public AdamantTransferMessageFactory(
            AdamantMarkdownProcessor adamantAddressProcessor,
            Encryptor encryptor,
            AdamantApiWrapper api,
            PublicKeyStorage publicKeyStorage,
            Avatar avatar
    ) {
        this.adamantAddressProcessor = adamantAddressProcessor;
        this.avatar = avatar;
        this.encryptor = encryptor;
        this.api = api;
        this.publicKeyStorage = publicKeyStorage;
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
        return new AdamantTransferMessageProcessor(api, encryptor, publicKeyStorage);
    }
}
