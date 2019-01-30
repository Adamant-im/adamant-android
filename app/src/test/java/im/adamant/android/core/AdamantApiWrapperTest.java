package im.adamant.android.core;

import com.goterl.lazycode.lazysodium.utils.KeyPair;

import org.junit.Assert;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import im.adamant.android.TestConstants;
import im.adamant.android.core.encryption.AdamantKeyGenerator;
import im.adamant.android.core.entities.Account;
import im.adamant.android.core.responses.Authorization;
import io.reactivex.Flowable;
import io.reactivex.Observable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AdamantApiWrapperTest {
    public static final int THREAD_COUNT = 100;
    public static final int TASK_COUNT = 100_000;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    AdamantApiBuilder apiBuilder;

    @Mock
    AdamantKeyGenerator keyGenerator;

    @Mock
    AdamantApi api;

    @Ignore
    @Test
    public void multiThreadingReadAndWriteAuthorization() throws Exception {
        configureMocks();

        AdamantApiWrapper wrapper = new AdamantApiWrapper(apiBuilder, keyGenerator);
        provideAuthorizationRunnable(wrapper).call();

        List<Callable<Boolean>> tasks = new ArrayList<>(Collections.nCopies(TASK_COUNT, provideAuthorizationRunnable(wrapper)));
        tasks.addAll(Collections.nCopies(TASK_COUNT, provideUpdateBalanceRunnable(wrapper)));
        Collections.shuffle(tasks);

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<Boolean>> futures = executorService.invokeAll(tasks);

        for (Future<Boolean> result : futures){
            Boolean authorized = result.get();
            Assert.assertTrue(authorized);
        }

    }

    private Callable<Boolean> provideAuthorizationRunnable(AdamantApiWrapper wrapper) {
        return () -> {
            wrapper
                    .authorize(TestConstants.testPassPhrase)
                    .blockingLast();

            return wrapper.isAuthorized();
        };
    }

    private Callable<Boolean> provideUpdateBalanceRunnable(AdamantApiWrapper wrapper) {
        return () -> {
            wrapper
                    .updateBalance()
                    .blockingAwait();

            return wrapper.isAuthorized();
        };
    }

    private void configureMocks() {
        Observable<AdamantApi> adamantApiObservable = buildMockedApi();

        when(apiBuilder.build()).thenReturn(adamantApiObservable);
        when(keyGenerator.getKeyPairFromPassPhrase(TestConstants.testPassPhrase))
                .thenReturn(new KeyPair(TestConstants.testPublicKey, TestConstants.testSecretKey));
    }

    private Observable<AdamantApi> buildMockedApi() {
        Account account = new Account();
        account.setAddress(TestConstants.testAddress);
        account.setBalance(TestConstants.testBalance);
        account.setUnconfirmedBalance(TestConstants.testBalance);
        account.setPublicKey(TestConstants.testPublicKey);

        Authorization authorization = new Authorization();
        authorization.setAccount(account);
        authorization.setSuccess(true);

        when(api.authorize(any(String.class))).thenReturn(Flowable.just(authorization));

        return Observable.just(api);
    }
}
