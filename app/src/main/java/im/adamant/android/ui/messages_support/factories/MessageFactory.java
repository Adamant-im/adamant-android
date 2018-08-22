package im.adamant.android.ui.messages_support.factories;

import android.view.ViewGroup;

import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import im.adamant.android.ui.messages_support.holders.AbstractMessageViewHolder;
import im.adamant.android.ui.messages_support.builders.MessageBuilder;
import im.adamant.android.ui.messages_support.processors.MessageProcessor;

public interface MessageFactory<T extends AbstractMessage> {
    MessageBuilder<T> getMessageBuilder();
    AbstractMessageViewHolder getViewHolder(ViewGroup parent);
    MessageProcessor<T> getMessageProcessor();
}
