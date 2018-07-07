package im.adamant.android.ui.messages_support.factories;

import android.view.ViewGroup;

import im.adamant.android.ui.entities.messages.AbstractMessage;
import im.adamant.android.ui.holders.messages.AbstractMessageViewHolder;
import im.adamant.android.ui.messages_support.builders.MessageBuilder;

public interface MessageFactory<T extends AbstractMessage> {
    MessageBuilder<T> getMessageBuilder();
    AbstractMessageViewHolder getViewHolder(ViewGroup parent);
}
