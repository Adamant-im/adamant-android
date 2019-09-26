package im.adamant.android.interactors.push;

import android.app.usage.UsageEvents;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.spongycastle.util.encoders.Base64Encoder;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import im.adamant.android.BuildConfig;
import im.adamant.android.R;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.transaction_assets.TransactionAsset;
import im.adamant.android.core.entities.transaction_assets.TransactionChatAsset;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.helpers.Settings;
import im.adamant.android.ui.entities.Contact;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.entities.AdamantPushSubscriptionMessage;
import im.adamant.android.ui.messages_support.factories.AdamantPushSubscriptionMessageFactory;
import im.adamant.android.ui.messages_support.factories.MessageFactoryProvider;
import im.adamant.android.ui.messages_support.processors.MessageProcessor;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.CompletableSubject;
import io.reactivex.subjects.PublishSubject;

public class FCMNotificationServiceFacade implements PushNotificationServiceFacade {
    private Gson gson;
    private Settings settings;
    private MessageFactoryProvider messageFactoryProvider;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public FCMNotificationServiceFacade(
            Gson gson,
            Settings settings,
            MessageFactoryProvider messageFactoryProvider
    ) {
        this.gson = gson;
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

            try {
                AdamantPushSubscriptionMessageFactory subscribeFactory = (AdamantPushSubscriptionMessageFactory)messageFactoryProvider
                        .getFactoryByType(SupportedMessageListContentType.ADAMANT_SUBSCRIBE_ON_NOTIFICATION);
                MessageProcessor<AdamantPushSubscriptionMessage> messageProcessor = subscribeFactory.getMessageProcessor();

                AdamantPushSubscriptionMessage adamantPushSubscribeMessage = preparePushMessage(deviceToken, AdamantPushSubscriptionMessage.ADD_ACTION);
                Single<Transaction<? extends TransactionAsset>> subscribeTransaction = messageProcessor.buildNormalizedTransaction(adamantPushSubscribeMessage);


                AdamantPushSubscriptionMessage adamantPushUnSubscribeMessage = preparePushMessage(deviceToken, AdamantPushSubscriptionMessage.REMOVE_ACTION);
                Single<Transaction<? extends TransactionAsset>> unsubscribeTransaction = messageProcessor.buildNormalizedTransaction(adamantPushUnSubscribeMessage);

                Disposable subscribe = messageProcessor
                        .sendTransaction(subscribeTransaction)
                        .doOnSuccess((transactionWasProcessed) -> {
                            if (transactionWasProcessed.isSuccess()) {
                                settings.setNotificationToken(deviceToken);
                                completable.onComplete();
                            } else {
                                completable.onError(new Exception(transactionWasProcessed.getError()));
                            }
                        })
                        .flatMap(transactionWasProcessed -> unsubscribeTransaction
                                .map(transaction -> gson.toJson(transaction)))
                        .subscribe(
                                // Let's save the transaction for unsubscribe, in case you need to unsubscribe without having keys, for example on the pin code screen
                                json -> settings.setUnsubscribeFcmTransaction(json),
                                completable::onError
                        );

                compositeDisposable.add(subscribe);

            } catch (Exception e) {
                completable.onError(e);
            }
        });

        return completable;
    }

    @Override
    public Completable unsubscribe() {
        String unsubscribeTransactionJson = settings.getUnsubscribeFcmTransaction();
        if (unsubscribeTransactionJson == null || unsubscribeTransactionJson.isEmpty()) {
            //TODO: Обязательно проверь в тестах кейс с выходом
            return Completable.complete();
        }

        try {

            Transaction<TransactionChatAsset> transaction = gson.fromJson(
                    unsubscribeTransactionJson,
                    new TypeToken<Transaction<TransactionChatAsset>>() {}.getType()
            );

            AdamantPushSubscriptionMessageFactory subscribeFactory = (AdamantPushSubscriptionMessageFactory)messageFactoryProvider
                    .getFactoryByType(SupportedMessageListContentType.ADAMANT_SUBSCRIBE_ON_NOTIFICATION);
            MessageProcessor<AdamantPushSubscriptionMessage> messageProcessor = subscribeFactory.getMessageProcessor();

            return messageProcessor
                    .sendTransaction(Single.just(transaction))
                    .ignoreElement()
                    .doOnComplete(() -> {
                        settings.setNotificationToken("");
                        settings.setUnsubscribeFcmTransaction("");
                    });
        } catch (Exception ex) {
            return Completable.error(ex);
        }

    }

    private AdamantPushSubscriptionMessage preparePushMessage(String token, String action) {
        AdamantPushSubscriptionMessage message = new AdamantPushSubscriptionMessage();
        message.setProvider("fcm");
        message.setToken(token);
        message.setCompanionId(settings.getAddressOfNotificationService());
        message.setSupportedType(SupportedMessageListContentType.ADAMANT_SUBSCRIBE_ON_NOTIFICATION);
        message.setAction(action);

        return message;
    }

    @Override
    protected void finalize() throws Throwable {
        if (compositeDisposable != null){
            compositeDisposable.dispose();
        }
        super.finalize();
    }
}
