package im.adamant.android.cases.core;

import com.goterl.lazycode.lazysodium.utils.KeyPair;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import im.adamant.android.BuildConfig;
import im.adamant.android.TestConstants;
import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.DefaultAdamantApiBuilderImpl;
import im.adamant.android.core.encryption.AdamantKeyGenerator;
import im.adamant.android.core.entities.Account;
import im.adamant.android.core.responses.Authorization;
import im.adamant.android.core.responses.ChatList;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subscribers.TestSubscriber;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AdamantApiWrapperSwitchTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();


    @Mock
    DefaultAdamantApiBuilderImpl apiBuilder;

    @Mock
    AdamantKeyGenerator keyGenerator;

    AdamantApi mockedApi;

    AtomicInteger counter = new AtomicInteger(0);

    CompositeDisposable subscritions = new CompositeDisposable();

    ChatList chatList;

    @Before
    public void before() {
        chatList = new ChatList();
        chatList.setSuccess(true);
        chatList.setNodeTimestamp(260000);
        chatList.setChats(new ArrayList<>());
        chatList.setCount(0);
    }

    @Test
    public void switchNewNodeAfterManyFailures() throws Exception {
        when(apiBuilder.build())
                .thenReturn(
                        Flowable.defer(this::buildMockedApiWithErrorChatLoading).toObservable()
                );
        when(keyGenerator.getKeyPairFromPassPhrase(TestConstants.TEST_PASS_PHRASE))
                .thenReturn(new KeyPair(TestConstants.TEST_PUBLIC_KEY, TestConstants.TEST_SECRET_KEY));

        TestScheduler scheduler = new TestScheduler();
        AdamantApiWrapper wrapper = new AdamantApiWrapper(apiBuilder, keyGenerator, scheduler);

        wrapper
                .authorize(TestConstants.TEST_PASS_PHRASE)
                .subscribe();
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);

        TestSubscriber<ChatList> subscriber = new TestSubscriber<>();

        wrapper
                .getChats(AdamantApi.ORDER_BY_TIMESTAMP_ASC)
                .subscribe(subscriber);

        scheduler.advanceTimeBy(AdamantApi.SYNCHRONIZE_DELAY_SECONDS * (2 + 1), TimeUnit.SECONDS);
        subscriber.assertValue(chatList);
    }

    @After
    public void after() {
        subscritions.dispose();
        subscritions.clear();
    }

    private HttpException newHttpException() {
        SecureRandom random = new SecureRandom();
        return new HttpException(
                Response.error(
                        500,
                        ResponseBody.create(
                                MediaType.parse("application/json"),
                                "{\"code\": " + random.nextInt() + "}"
                        )
                )
        );
    }

    private Flowable<AdamantApi> buildMockedApiWithErrorChatLoading() {
        Account account = new Account();
        account.setAddress(TestConstants.TEST_ADDRESS);
        account.setBalance(TestConstants.TEST_BALANCE);
        account.setUnconfirmedBalance(TestConstants.TEST_BALANCE);
        account.setPublicKey(TestConstants.TEST_PUBLIC_KEY);

        Authorization authorization = new Authorization();
        authorization.setAccount(account);
        authorization.setSuccess(true);

        mockedApi = mock(AdamantApi.class);

        int cnt = counter.get();
        if (cnt < 1) {
            when(mockedApi.authorize(any(String.class)))
                    .thenReturn(Flowable.just(authorization));

            when(
                    mockedApi.getChats(
                        any(String.class),
                        any(String.class)
                    )
            )
            .thenReturn(
                    Flowable.error(newHttpException())
            );
        } else {
            when(
                    mockedApi.getChats(
                            any(String.class),
                            any(String.class)
                    )
            )
            .thenReturn(
                    Flowable.just(chatList)
            );
        }

        counter.incrementAndGet();

        return Flowable.just(mockedApi);
    }
}
