package im.adamant.android.interactors.push;

import android.app.usage.UsageEvents;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import im.adamant.android.BuildConfig;
import im.adamant.android.R;
import im.adamant.android.helpers.Settings;
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
import io.reactivex.subjects.CompletableSubject;
import io.reactivex.subjects.PublishSubject;

public class FCMNotificationServiceFacade implements PushNotificationServiceFacade {
    private Settings settings;
    private MessageFactoryProvider messageFactoryProvider;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public FCMNotificationServiceFacade(
            Settings settings,
            MessageFactoryProvider messageFactoryProvider
    ) {
        this.settings = settings;
        this.messageFactoryProvider = messageFactoryProvider;
    }

    @Override
    public int getTitleResource() {
        return R.string.fcm_notification_service_full;
    }

    @Override
    public int getShortTitleResource() {
        return R.string.fcm_notification_service_short;
    }

    @Override
    public int getDescriptionResource() {
        return R.string.fcm_notification_service_description;
    }

    @Override
    public SupportedPushNotificationFacadeType getFacadeType() {
        return SupportedPushNotificationFacadeType.FCM;
    }

    @Override
    public Completable subscribe() {
        CompletableSubject completable = CompletableSubject.create();
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            String deviceToken = instanceIdResult.getToken();

            String oldDeviceToken = settings.getNotificationToken();

            if (deviceToken.isEmpty() || deviceToken.equalsIgnoreCase(oldDeviceToken)){
                 completable.onComplete();
                 return;
            }

            Disposable subscribe = sendMessageForNotificationService(deviceToken, AdamantPushSubscriptionMessage.ADD_ACTION)
                    .subscribe(
                            () -> {
                                settings.setNotificationToken(deviceToken);
                                completable.onComplete();
                            },
                            completable::onError
                    );

            compositeDisposable.add(subscribe);

        });

        return completable;
    }

    @Override
    public Completable unsubscribe() {
        String notificationToken = settings.getNotificationToken();
        if (notificationToken == null || notificationToken.isEmpty()) {
            //TODO: Обязательно проверь в тестах кейс с выходом
            return Completable.complete();
        }

        return sendMessageForNotificationService(notificationToken, AdamantPushSubscriptionMessage.REMOVE_ACTION)
                .doOnComplete(() -> {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                    settings.setNotificationToken("");
                });
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

    @Override
    protected void finalize() throws Throwable {
        if (compositeDisposable != null){
            compositeDisposable.dispose();
        }
        super.finalize();
    }
}
