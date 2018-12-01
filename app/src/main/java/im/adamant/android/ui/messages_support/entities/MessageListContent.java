package im.adamant.android.ui.messages_support.entities;

import im.adamant.android.ui.messages_support.SupportedMessageListContentType;

public interface MessageListContent {
    SupportedMessageListContentType getSupportedType();
    void setSupportedType(SupportedMessageListContentType supportedType);
    String getCompanionId();
    void setCompanionId(String companionId);
    long getTimestamp();
    void setTimestamp(long timestamp);
}
