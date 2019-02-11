package im.adamant.android.interactors;

import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.responses.TransactionWasProcessed;
import im.adamant.android.helpers.Settings;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.entities.AdamantPushSubscriptionMessage;
import im.adamant.android.ui.messages_support.factories.AdamantPushSubscriptionMessageFactory;
import im.adamant.android.ui.messages_support.factories.MessageFactoryProvider;
import im.adamant.android.ui.messages_support.processors.MessageProcessor;
import io.reactivex.Completable;

public class SubscribeToPushInteractor {
    private Settings settings;
    private MessageFactoryProvider messageFactoryProvider;

    public SubscribeToPushInteractor(
            Settings settings,
            MessageFactoryProvider messageFactoryProvider
    ) {
        this.settings = settings;
        this.messageFactoryProvider = messageFactoryProvider;
    }

    public void enablePush(boolean enable) {
        settings.setEnablePushNotifications(enable);
    }


    public Completable savePushToken(String currentToken) {
        String oldDeviceToken = settings.getNotificationToken();

        if (!settings.isEnablePushNotifications()){
            return Completable.complete();
        }

        if (currentToken == null || currentToken.isEmpty() || currentToken.equalsIgnoreCase(oldDeviceToken)){
            return Completable.complete();
        }

        return sendMessageForNotificationService(currentToken, AdamantPushSubscriptionMessage.ADD_ACTION)
                .doOnComplete(() -> settings.setNotificationToken(currentToken));
    }

    public Completable deleteCurrentToken() {
        String notificationToken = settings.getNotificationToken();
        if (notificationToken == null || notificationToken.isEmpty()) { return Completable.complete(); }

        return sendMessageForNotificationService(notificationToken, AdamantPushSubscriptionMessage.REMOVE_ACTION)
                .doOnComplete(() -> {
                    settings.setNotificationToken("");
                    settings.setEnablePushNotifications(false);
                });
    }

    public boolean isEnabledPush() {
        return settings.isEnablePushNotifications();
    }

    private Completable sendMessageForNotificationService(String token, String action) {
        try {
            AdamantPushSubscriptionMessageFactory subscribeFactory = (AdamantPushSubscriptionMessageFactory)messageFactoryProvider
                    .getFactoryByType(SupportedMessageListContentType.ADAMANT_SUBSCRIBE_ON_NOTIFICATION);

            AdamantPushSubscriptionMessage message = new AdamantPushSubscriptionMessage();
            message.setProvider("fcm");
            message.setToken(token);
            message.setCompanionId(settings.getAddressOfNotificationService());
            message.setSupportedType(SupportedMessageListContentType.ADAMANT_SUBSCRIBE_ON_NOTIFICATION);
            message.setAction(action);

            MessageProcessor<AdamantPushSubscriptionMessage> messageProcessor = subscribeFactory.getMessageProcessor();

            return messageProcessor
                    .sendMessage(message)
                    .ignoreElement();

        } catch (Exception e) {
            return Completable.error(e);
        }
    }
}
