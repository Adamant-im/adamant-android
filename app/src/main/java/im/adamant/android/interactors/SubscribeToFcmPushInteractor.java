package im.adamant.android.interactors;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import im.adamant.android.BuildConfig;
import im.adamant.android.helpers.Settings;
import im.adamant.android.rx.Irrelevant;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.entities.AdamantPushSubscriptionMessage;
import im.adamant.android.ui.messages_support.factories.AdamantPushSubscriptionMessageFactory;
import im.adamant.android.ui.messages_support.factories.MessageFactoryProvider;
import im.adamant.android.ui.messages_support.processors.MessageProcessor;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

public class SubscribeToFcmPushInteractor {
    private Settings settings;
    private MessageFactoryProvider messageFactoryProvider;
    private PublishSubject<Event> subscribePublisher = PublishSubject.create();
    private Flowable<Event> subscribeFlowable = subscribePublisher.toFlowable(BackpressureStrategy.LATEST);
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public enum Event {
        SUBSCRIBED,
        UNSUBSCRIBED,
        IGNORED
    }

    public SubscribeToFcmPushInteractor(
            Settings settings,
            MessageFactoryProvider messageFactoryProvider
    ) {
        this.settings = settings;
        this.messageFactoryProvider = messageFactoryProvider;
    }

    public void enablePush(boolean enable) {
        settings.setEnablePushNotifications(enable);
    }

    public boolean isEnabledPush() {
        return settings.isEnablePushNotifications();
    }

    public Flowable<Event> getEventsObservable() {
        return subscribeFlowable;
    }

    public void savePushToken(String currentToken) {
        String oldDeviceToken = settings.getNotificationToken();

        if (!settings.isEnablePushNotifications()){
            subscribePublisher.onNext(Event.IGNORED);
        }

        if (currentToken == null || currentToken.isEmpty() || currentToken.equalsIgnoreCase(oldDeviceToken)){
            subscribePublisher.onNext(Event.IGNORED);
        }

        Disposable subscription = sendMessageForNotificationService(currentToken, AdamantPushSubscriptionMessage.ADD_ACTION)
                .subscribe(() -> {
                            settings.setNotificationToken(currentToken);
                            subscribePublisher.onNext(Event.SUBSCRIBED);
                        },
                        (error) -> subscribePublisher.onError(error)
                );
        compositeDisposable.add(subscription);
    }

    public void deleteCurrentToken() {
        String notificationToken = settings.getNotificationToken();
        if (notificationToken == null || notificationToken.isEmpty()) {
            subscribePublisher.onNext(Event.IGNORED);
        }

        Disposable subscription = sendMessageForNotificationService(notificationToken, AdamantPushSubscriptionMessage.REMOVE_ACTION)
                .subscribe(() -> {
                            settings.setNotificationToken("");
                            settings.setEnablePushNotifications(false);
                            subscribePublisher.onNext(Event.UNSUBSCRIBED);
                        },
                        (error) -> subscribePublisher.onError(error)
                );
        compositeDisposable.add(subscription);
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
                    .retryWhen((throwable) -> throwable
                            .filter((error) -> error instanceof IOException)
                            .delay(BuildConfig.UPDATE_BALANCE_SECONDS_DELAY, TimeUnit.SECONDS)
                    )
                    .ignoreElement();

        } catch (Exception e) {
            return Completable.error(e);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (compositeDisposable != null){
            compositeDisposable.dispose();
        }
        super.finalize();
    }
}
