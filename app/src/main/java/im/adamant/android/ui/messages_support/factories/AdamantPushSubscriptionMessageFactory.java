package im.adamant.android.ui.messages_support.factories;

import android.view.ViewGroup;

import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.Encryptor;
import im.adamant.android.ui.messages_support.builders.MessageBuilder;
import im.adamant.android.ui.messages_support.entities.AdamantPushSubscriptionMessage;
import im.adamant.android.ui.messages_support.holders.AbstractMessageListContentViewHolder;
import im.adamant.android.ui.messages_support.processors.AdamantPushSubsciptionMessageProcessor;
import im.adamant.android.ui.messages_support.processors.MessageProcessor;


public class AdamantPushSubscriptionMessageFactory implements MessageFactory<AdamantPushSubscriptionMessage> {
    private Encryptor encryptor;
    private AdamantApiWrapper api;

    public AdamantPushSubscriptionMessageFactory(Encryptor encryptor, AdamantApiWrapper api) {
        this.encryptor = encryptor;
        this.api = api;
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
        return new AdamantPushSubsciptionMessageProcessor(api, encryptor);
    }
}
