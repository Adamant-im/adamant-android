package im.adamant.android.interactors;

import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.responses.TransactionWasProcessed;
import im.adamant.android.helpers.Settings;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.entities.AdamantPushSubscriptionMessage;
import im.adamant.android.ui.messages_support.factories.AdamantPushSubscriptionMessageFactory;
import im.adamant.android.ui.messages_support.factories.MessageFactoryProvider;
import io.reactivex.Completable;

public class SubscribeToPushInteractor {
    private Settings settings;
    private AdamantApiWrapper api;
    private MessageFactoryProvider messageFactoryProvider;
    private SendMessageInteractor sendMessageInteractor;

    public SubscribeToPushInteractor(
            Settings settings,
            AdamantApiWrapper api,
            MessageFactoryProvider messageFactoryProvider,
            SendMessageInteractor sendMessageInteractor
    ) {
        this.settings = settings;
        this.api = api;
        this.messageFactoryProvider = messageFactoryProvider;
        this.sendMessageInteractor = sendMessageInteractor;
    }

    public void savePushConfig(boolean enable, String address) {
        settings.setEnablePushNotifications(enable);
        settings.setAddressOfNotificationService(address);
    }

    public Completable savePushToken(String currentToken) {
        String oldDeviceToken = settings.getNotificationToken();

        if (!settings.isEnablePushNotifications()){
            return Completable.complete();
        }

        if (currentToken == null || currentToken.isEmpty() || currentToken.equalsIgnoreCase(oldDeviceToken)){
            return Completable.complete();
        }

        try {
            AdamantPushSubscriptionMessageFactory subscribeFactory = (AdamantPushSubscriptionMessageFactory)messageFactoryProvider
                    .getFactoryByType(SupportedMessageListContentType.ADAMANT_SUBSCRIBE_ON_NOTIFICATION);

            AdamantPushSubscriptionMessage message = new AdamantPushSubscriptionMessage();
            message.setProvider("fcm");
            message.setToken(currentToken);
            message.setCompanionId(settings.getAddressOfNotificationService());
            message.setSupportedType(SupportedMessageListContentType.ADAMANT_SUBSCRIBE_ON_NOTIFICATION);

            Settings localSettings = settings;

            return sendMessageInteractor
                    .sendMessage(subscribeFactory.getMessageProcessor(), message)
                    .doOnSuccess(transactionWasProcessed -> localSettings.setNotificationToken(currentToken))
                    .onErrorReturn(error -> new TransactionWasProcessed())
                    .toCompletable();

        } catch (Exception e) {
            return Completable.error(e);
        }
    }

    public boolean isEnabledPush() {
        return settings.isEnablePushNotifications();
    }

    public String getPushServiceAddress() {
        return settings.getAddressOfNotificationService();
    }
}
