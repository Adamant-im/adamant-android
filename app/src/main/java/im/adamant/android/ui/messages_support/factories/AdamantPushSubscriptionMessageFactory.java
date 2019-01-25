package im.adamant.android.ui.messages_support.factories;

import android.view.ViewGroup;

import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.Encryptor;
import im.adamant.android.helpers.PublicKeyStorage;
import im.adamant.android.ui.messages_support.builders.MessageBuilder;
import im.adamant.android.ui.messages_support.entities.AdamantPushSubscriptionMessage;
import im.adamant.android.ui.messages_support.holders.AbstractMessageListContentViewHolder;
import im.adamant.android.ui.messages_support.processors.AdamantPushSubscriptionMessageProcessor;
import im.adamant.android.ui.messages_support.processors.MessageProcessor;


public class AdamantPushSubscriptionMessageFactory implements MessageFactory<AdamantPushSubscriptionMessage> {
    private Encryptor encryptor;
    private AdamantApiWrapper api;
    private PublicKeyStorage publicKeyStorage;

    public AdamantPushSubscriptionMessageFactory(Encryptor encryptor, AdamantApiWrapper api, PublicKeyStorage publicKeyStorage) {
        this.encryptor = encryptor;
        this.api = api;
        this.publicKeyStorage = publicKeyStorage;
    }

    //No need implementation
    @Override
    public MessageBuilder<AdamantPushSubscriptionMessage> getMessageBuilder() {
        return null;
    }

    //No need implementation
    @Override
    public AbstractMessageListContentViewHolder getViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public MessageProcessor<AdamantPushSubscriptionMessage> getMessageProcessor() {
        return new AdamantPushSubscriptionMessageProcessor(api, encryptor, publicKeyStorage);
    }
}
