package im.adamant.android.ui.messages_support.entities;

import im.adamant.android.ui.messages_support.SupportedMessageListContentType;

public class Separator implements MessageListContent {
    private SupportedMessageListContentType supportedMessageListContentType = SupportedMessageListContentType.SEPARATOR;
    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public SupportedMessageListContentType getSupportedType() {
        return supportedMessageListContentType;
    }

    @Override
    public void setSupportedType(SupportedMessageListContentType supportedType) {

    }

    @Override
    public String getCompanionId() {
        return null;
    }

    @Override
    public void setCompanionId(String companionId) {

    }
}
