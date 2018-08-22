package im.adamant.android.ui.messages_support;

public enum SupportedMessageTypes {
    ADAMANT_BASIC,
    ADAMANT_SIGNAL,
    ETHEREUM_TRANSFER,
    FALLBACK,
    UNDEFINED // Causes an exception as a protection against developer inattention
}
