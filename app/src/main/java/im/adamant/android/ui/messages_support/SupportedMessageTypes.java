package im.adamant.android.ui.messages_support;

public enum SupportedMessageTypes {
    ADAMANT_BASIC,
    ADAMANT_SUBSCRIBE_ON_NOTIFICATION,
    ETHEREUM_TRANSFER,
    FALLBACK,
    UNDEFINED // Causes an exception as a protection against developer inattention
}
