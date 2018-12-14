package im.adamant.android.ui.messages_support;

public enum SupportedMessageListContentType {
    ADAMANT_BASIC,
    ADAMANT_TRANSFER_MESSAGE,
    ADAMANT_SUBSCRIBE_ON_NOTIFICATION,
    ETHEREUM_TRANSFER,
    BINANCE_TRANSFER, //TODO: May be refactor to ERC20
    FALLBACK,
    SEPARATOR,
    UNDEFINED // Causes an exception as a protection against developer inattention
}
