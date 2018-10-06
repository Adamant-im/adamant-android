package im.adamant.android.ui.messages_support;

public enum SupportedMessageListContentType {
    ADAMANT_BASIC,
    ADAMANT_SUBSCRIBE_ON_NOTIFICATION,
    ETHEREUM_TRANSFER,
    FALLBACK,
    SEPARATOR,
    UNDEFINED // Causes an exception as a protection against developer inattention
}
