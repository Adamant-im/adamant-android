package im.adamant.android.cases.interactors;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.TimeUnit;

import im.adamant.android.TestApplication;
import im.adamant.android.core.entities.transaction_assets.TransactionChatAsset;
import im.adamant.android.core.responses.TransactionWasNormalized;
import im.adamant.android.helpers.Settings;
import im.adamant.android.interactors.push.FCMNotificationServiceFacade;
import im.adamant.android.shadows.FirebaseInstanceIdShadow;
import im.adamant.android.shadows.LocaleChangerShadow;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.factories.AdamantPushSubscriptionMessageFactory;
import im.adamant.android.ui.messages_support.factories.MessageFactoryProvider;
import im.adamant.android.ui.messages_support.processors.AdamantPushSubscriptionMessageProcessor;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(
        sdk = Config.TARGET_SDK,
        manifest = Config.NONE,
        shadows = {
                LocaleChangerShadow.class,
                FirebaseInstanceIdShadow.class
        },
        application = TestApplication.class
)
public class FCMNotificationServiceFacadeTest {
    private Gson gson;
    private Settings settings;
    private MessageFactoryProvider messageFactoryProvider;
    private AdamantPushSubscriptionMessageFactory pushSubscriptionMessageFactory;
    private AdamantPushSubscriptionMessageProcessor processor;
    private FCMNotificationServiceFacade facade;

    @Before
    public void setUp() throws Exception {
        gson = mock(Gson.class);
        settings = mock(Settings.class);
        messageFactoryProvider = mock(MessageFactoryProvider.class);
        pushSubscriptionMessageFactory = mock(AdamantPushSubscriptionMessageFactory.class);
        processor = mock(AdamantPushSubscriptionMessageProcessor.class);

        facade = new FCMNotificationServiceFacade(gson, settings, messageFactoryProvider);

        when(messageFactoryProvider
                .getFactoryByType(SupportedMessageListContentType.ADAMANT_SUBSCRIBE_ON_NOTIFICATION))
                .thenReturn(pushSubscriptionMessageFactory);

        when(pushSubscriptionMessageFactory.getMessageProcessor()).thenReturn(processor);
    }

    @Test
    public void testSubscribeHttpExceptionFail() {
        when(processor.sendMessage(any())).thenReturn(Single.error(mock(HttpException.class)));

       facade
           .subscribe()
           .timeout(1, TimeUnit.SECONDS)
           .test()
           .assertError(HttpException.class);

    }
}
