package im.adamant.android.cases.core;

import com.goterl.lazycode.lazysodium.utils.KeyPair;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.OngoingStubbing;

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
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.transaction_assets.TransactionChatAsset;
import im.adamant.android.core.entities.transaction_assets.TransactionStateAsset;
import im.adamant.android.core.requests.NewAccount;
import im.adamant.android.core.requests.ProcessTransaction;
import im.adamant.android.core.responses.Authorization;
import im.adamant.android.core.responses.ChatList;
import im.adamant.android.core.responses.MessageList;
import im.adamant.android.core.responses.OperationComplete;
import im.adamant.android.core.responses.ParametrizedTransactionList;
import im.adamant.android.core.responses.PublicKeyResponse;
import im.adamant.android.core.responses.TransactionDetailsResponse;
import im.adamant.android.core.responses.TransactionList;
import im.adamant.android.core.responses.TransactionWasNormalized;
import im.adamant.android.core.responses.TransactionWasProcessed;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subscribers.TestSubscriber;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

import static im.adamant.android.Constants.KVS_CONTACT_LIST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AdamantApiWrapperSwitchOnFailureTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public final TestName testNameRule = new TestName();

    @Mock
    DefaultAdamantApiBuilderImpl apiBuilder;

    @Mock
    AdamantKeyGenerator keyGenerator;

    AdamantApiWrapper wrapper;

    AdamantApi mockedApi;
    AtomicInteger counter;
    CompositeDisposable subscritions = new CompositeDisposable();
    TestScheduler scheduler;

    //Stubs
    ChatList chatList;
    TransactionList transactionList;
    MessageList messageList;
    PublicKeyResponse publicKeyResponse;
    TransactionWasNormalized transactionWasNormalized;
    TransactionWasProcessed transactionWasProcessed;
    OperationComplete operationComplete;
    ParametrizedTransactionList parametrizedTransactionList;
    TransactionDetailsResponse transactionDetailsResponse;
    Authorization authorization;

    @Before
    public void before() {
        initStubs();

        counter = new AtomicInteger(0);
        scheduler = new TestScheduler();

        when(apiBuilder.build())
                .thenReturn(
                        Flowable.defer(() -> buildMockedApiWithErrorChatLoading(testNameRule.getMethodName())).toObservable()
                );
        when(keyGenerator.getKeyPairFromPassPhrase(TestConstants.TEST_PASS_PHRASE))
                .thenReturn(new KeyPair(TestConstants.TEST_PUBLIC_KEY, TestConstants.TEST_SECRET_KEY));

        wrapper = new AdamantApiWrapper(apiBuilder, keyGenerator, scheduler);

        Disposable subscribe = wrapper
                .authorize(TestConstants.TEST_PASS_PHRASE)
                .subscribe();
        subscritions.add(subscribe);

        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
    }

    @Test
    public void testSwitchNodeIfErrorOnAuthorize() throws Exception {
        TestSubscriber<Authorization> subscriber = new TestSubscriber<>();

        when(mockedApi.authorize(any(String.class)))
                .thenReturn(Flowable.just(authorization));

        wrapper
                .authorize(TestConstants.TEST_PASS_PHRASE)
                .retryWhen(flowable -> flowable.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS,TimeUnit.SECONDS, scheduler))
                .subscribe(subscriber);

        scheduler.advanceTimeBy(AdamantApi.SYNCHRONIZE_DELAY_SECONDS * (2 + 1), TimeUnit.SECONDS);
        subscriber.assertComplete();
    }

    @Test
    public void testSwitchNodeIfErrorOnGetMessageTransactionsByHeightAndOffset() throws Exception {
        TestSubscriber<TransactionList> subscriber = new TestSubscriber<>();

        wrapper
                .getMessageTransactionsByHeightAndOffset(1, 0, AdamantApi.ORDER_BY_TIMESTAMP_ASC)
                .subscribe(subscriber);

        scheduler.advanceTimeBy(AdamantApi.SYNCHRONIZE_DELAY_SECONDS * (2 + 1), TimeUnit.SECONDS);
        subscriber.assertValue(transactionList);
    }

    @Test
    public void testSwitchNodeIfErrorOnGetChatsByOffset() throws Exception {
        TestSubscriber<ChatList> subscriber = new TestSubscriber<>();

        wrapper
                .getChatsByOffset(1, 0, AdamantApi.ORDER_BY_TIMESTAMP_ASC)
                .subscribe(subscriber);

        scheduler.advanceTimeBy(AdamantApi.SYNCHRONIZE_DELAY_SECONDS * (2 + 1), TimeUnit.SECONDS);
        subscriber.assertValue(chatList);
    }

    @Test
    public void testSwitchNodeIfErrorOnGetMessagesByOffset() throws Exception {
        TestSubscriber<MessageList> subscriber = new TestSubscriber<>();

        wrapper
                .getMessagesByOffset(TestConstants.TEST_COMPANION_ADDRESS, 1, 0, AdamantApi.ORDER_BY_TIMESTAMP_ASC)
                .subscribe(subscriber);

        scheduler.advanceTimeBy(AdamantApi.SYNCHRONIZE_DELAY_SECONDS * (2 + 1), TimeUnit.SECONDS);
        subscriber.assertValue(messageList);
    }

    @Test
    public void testSwitchNodeIfErrorOnGetPublicKey() throws Exception {
        TestSubscriber<PublicKeyResponse> subscriber = new TestSubscriber<>();

        wrapper
                .getPublicKey(TestConstants.TEST_PUBLIC_KEY)
                .subscribe(subscriber);

        scheduler.advanceTimeBy(AdamantApi.SYNCHRONIZE_DELAY_SECONDS * (2 + 1), TimeUnit.SECONDS);
        subscriber.assertValue(publicKeyResponse);
    }

    @Test
    public void testSwitchNodeIfErrorOnProcessTransaction() throws Exception {
        TestSubscriber<TransactionWasProcessed> subscriber = new TestSubscriber<>();

        wrapper
                .processTransaction(new ProcessTransaction(new Transaction<TransactionChatAsset>()))
                .retryWhen(flowable -> flowable.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS,TimeUnit.SECONDS, scheduler))
                .subscribe(subscriber);

        scheduler.advanceTimeBy(AdamantApi.SYNCHRONIZE_DELAY_SECONDS * (2 + 1), TimeUnit.SECONDS);
        subscriber.assertValue(transactionWasProcessed);
    }

    @Test
    public void testSwitchNodeIfErrorOnCreateNewAccount() throws Exception {
        TestSubscriber<Authorization> subscriber = new TestSubscriber<>();

        wrapper
                .createNewAccount(TestConstants.TEST_PASS_PHRASE)
                .retryWhen(flowable -> flowable.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS,TimeUnit.SECONDS, scheduler))
                .subscribe(subscriber);

        scheduler.advanceTimeBy(AdamantApi.SYNCHRONIZE_DELAY_SECONDS * (2 + 1), TimeUnit.SECONDS);
        subscriber.assertValue(authorization);
    }

    @Test
    public void testSwitchNodeIfErrorOnSendToKeyValueStorage() throws Exception {
        TestSubscriber<OperationComplete> subscriber = new TestSubscriber<>();

        wrapper
                .sendToKeyValueStorage(new Transaction<>())
                .retryWhen(flowable -> flowable.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS,TimeUnit.SECONDS, scheduler))
                .subscribe(subscriber);

        scheduler.advanceTimeBy(AdamantApi.SYNCHRONIZE_DELAY_SECONDS * (2 + 1), TimeUnit.SECONDS);
        subscriber.assertValue(operationComplete);
    }

    @Test
    public void testSwitchNodeIfErrorOnGetFromKeyValueStorage() throws Exception {
        TestSubscriber<ParametrizedTransactionList> subscriber = new TestSubscriber<>();

        wrapper
                .getFromKeyValueStorage(TestConstants.TEST_ADDRESS, KVS_CONTACT_LIST, AdamantApi.ORDER_BY_TIMESTAMP_DESC, 1)
                .subscribe(subscriber);

        scheduler.advanceTimeBy(AdamantApi.SYNCHRONIZE_DELAY_SECONDS * (2 + 1), TimeUnit.SECONDS);
        subscriber.assertValue(parametrizedTransactionList);
    }

    @Test
    public void testSwitchNodeIfErrorOnGetAdamantAllFinanceTransactions_ArgumentsCount_1() throws Exception {
        TestSubscriber<TransactionList> subscriber = new TestSubscriber<>();

        wrapper
                .getAdamantAllFinanceTransactions(AdamantApi.ORDER_BY_TIMESTAMP_DESC)
                .subscribe(subscriber);

        scheduler.advanceTimeBy(AdamantApi.SYNCHRONIZE_DELAY_SECONDS * (2 + 1), TimeUnit.SECONDS);
        subscriber.assertValue(transactionList);
    }

    @Test
    public void testSwitchNodeIfErrorOnGetAdamantAllFinanceTransactions_ArgumentsCount_3() throws Exception {
        TestSubscriber<TransactionList> subscriber = new TestSubscriber<>();

        wrapper
                .getAdamantAllFinanceTransactions(1, 0, AdamantApi.ORDER_BY_TIMESTAMP_DESC)
                .subscribe(subscriber);

        scheduler.advanceTimeBy(AdamantApi.SYNCHRONIZE_DELAY_SECONDS * (2 + 1), TimeUnit.SECONDS);
        subscriber.assertValue(transactionList);
    }

    @Test
    public void testSwitchNodeIfErrorOnGetAdamantAllFinanceTransactions_ArgumentsCount_4() throws Exception {
        TestSubscriber<TransactionList> subscriber = new TestSubscriber<>();

        wrapper
                .getAdamantAllFinanceTransactions(Transaction.SEND, 1, 0, AdamantApi.ORDER_BY_TIMESTAMP_DESC)
                .subscribe(subscriber);

        scheduler.advanceTimeBy(AdamantApi.SYNCHRONIZE_DELAY_SECONDS * (2 + 1), TimeUnit.SECONDS);
        subscriber.assertValue(transactionList);
    }

    @Test
    public void testSwitchNodeIfErrorOnSendAdmTransferTransaction() throws Exception {
        TestSubscriber<TransactionWasProcessed> subscriber = new TestSubscriber<>();

        wrapper
                .sendAdmTransferTransaction(new ProcessTransaction(new Transaction<>()))
                .retryWhen(flowable -> flowable.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS,TimeUnit.SECONDS, scheduler))
                .subscribe(subscriber);

        scheduler.advanceTimeBy(AdamantApi.SYNCHRONIZE_DELAY_SECONDS * (2 + 1), TimeUnit.SECONDS);
        subscriber.assertValue(transactionWasProcessed);
    }

    @Test
    public void testSwitchNodeIfErrorOnGetTransactionDetails() throws Exception {
        TestSubscriber<TransactionDetailsResponse> subscriber = new TestSubscriber<>();

        wrapper
                .getTransactionDetails("0000000000000000000000000")
                .subscribe(subscriber);

        scheduler.advanceTimeBy(AdamantApi.SYNCHRONIZE_DELAY_SECONDS * (2 + 1), TimeUnit.SECONDS);
        subscriber.assertValue(transactionDetailsResponse);
    }

    @Test
    public void testSwitchNodeIfErrorOnUpdateBalance() throws Exception {
        TestObserver<Void> observer = wrapper
                .updateBalance()
                .retryWhen(flowable -> flowable.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS, TimeUnit.SECONDS, scheduler))
                .test();

        observer.assertNotComplete();

        scheduler.advanceTimeBy(AdamantApi.SYNCHRONIZE_DELAY_SECONDS * (2 + 1), TimeUnit.SECONDS);
        observer.assertComplete();
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

    private Flowable<AdamantApi> buildMockedApiWithErrorChatLoading(String testName) {

        mockedApi = mock(AdamantApi.class);

        switch (testName) {
            case "testSwitchNodeIfErrorOnGetMessageTransactionsByHeightAndOffset": {
                buildCallStub(
                        mockedApi,
                        when(mockedApi.getMessageTransactions(any(String.class), any(Integer.class), any(Integer.class), any(String.class))),
                        Flowable.just(transactionList),
                        Flowable.error(newHttpException())
                );
            }
            break;
            case "testSwitchNodeIfErrorOnGetChatsByOffset": {
                buildCallStub(
                        mockedApi,
                        when(mockedApi.getChatsByOffset(any(String.class), any(Integer.class), any(Integer.class), any(String.class))),
                        Flowable.just(chatList),
                        Flowable.error(newHttpException())
                );
            }
            break;
            case "testSwitchNodeIfErrorOnGetMessagesByOffset": {
                buildCallStub(
                        mockedApi,
                        when(mockedApi.getMessagesByOffset(any(String.class), any(String.class), any(Integer.class), any(Integer.class), any(String.class))),
                        Flowable.just(messageList),
                        Flowable.error(newHttpException())
                );
            }
            break;
            case "testSwitchNodeIfErrorOnGetPublicKey": {
                buildCallStub(
                        mockedApi,
                        when(mockedApi.getPublicKey(any(String.class))),
                        Flowable.just(publicKeyResponse),
                        Flowable.error(newHttpException())
                );
            }
            break;
            case "testSwitchNodeIfErrorOnProcessTransaction": {
                buildCallStub(
                        mockedApi,
                        when(mockedApi.processTransaction(any(ProcessTransaction.class))),
                        Flowable.just(transactionWasProcessed),
                        Flowable.error(newHttpException())
                );
            }
            break;
            case "testSwitchNodeIfErrorOnCreateNewAccount": {
                buildCallStub(
                        mockedApi,
                        when(mockedApi.createNewAccount(any(NewAccount.class))),
                        Flowable.just(authorization),
                        Flowable.error(newHttpException())
                );
            }
            break;
            case "testSwitchNodeIfErrorOnSendToKeyValueStorage": {
                buildCallStub(
                        mockedApi,
                        when(mockedApi.sendToKeyValueStorage(any(ProcessTransaction.class))),
                        Flowable.just(operationComplete),
                        Flowable.error(newHttpException())
                );
            }
            break;
            case "testSwitchNodeIfErrorOnGetFromKeyValueStorage": {
                buildCallStub(
                        mockedApi,
                        when(mockedApi.getFromKeyValueStorage(any(String.class), any(String.class), any(String.class), any(Integer.class))),
                        Flowable.just(parametrizedTransactionList),
                        Flowable.error(newHttpException())
                );
            }
            break;
            case "testSwitchNodeIfErrorOnGetAdamantAllFinanceTransactions_ArgumentsCount_1":
            case "testSwitchNodeIfErrorOnGetAdamantAllFinanceTransactions_ArgumentsCount_3":
            case "testSwitchNodeIfErrorOnGetAdamantAllFinanceTransactions_ArgumentsCount_4": {
                buildCallStub(
                        mockedApi,
                        when(mockedApi.getAdamantAllFinanceTransactions(
                                any(String.class),
                                any(Integer.class),
                                any(Integer.class),
                                any(Integer.class),
                                any(String.class),
                                any(Integer.class))
                        ),
                        Flowable.just(transactionList),
                        Flowable.error(newHttpException())
                );
            }
            break;
            case "testSwitchNodeIfErrorOnSendAdmTransferTransaction": {
                buildCallStub(
                        mockedApi,
                        when(mockedApi.sendAdmTransferTransaction(any(ProcessTransaction.class))),
                        Flowable.just(transactionWasProcessed),
                        Flowable.error(newHttpException())
                );
            }
            break;
            case "testSwitchNodeIfErrorOnGetTransactionDetails": {
                buildCallStub(
                        mockedApi,
                        when(mockedApi.getTransactionDetails(any(String.class))),
                        Flowable.just(transactionDetailsResponse),
                        Flowable.error(newHttpException())
                );
            }
            break;
            case "testSwitchNodeIfErrorOnUpdateBalance": {
                buildCallStub(
                        mockedApi,
                        when(mockedApi.authorize(any(String.class))),
                        Flowable.just(authorization),
                        Flowable.error(newHttpException())
                );
            }
            break;
        }

        return Flowable.just(mockedApi);
    }

    private <T> void buildCallStub(AdamantApi mockedApi, OngoingStubbing<T> methodCall, T successValue , T errorValue) {
        int cnt = counter.get();
        if (cnt < 1) {
            methodCall.thenReturn(errorValue);

            when(mockedApi.authorize(any(String.class)))
                    .thenReturn(Flowable.just(authorization));

        } else {
            methodCall.thenReturn(successValue);
        }

        counter.incrementAndGet();
    }

    private void initStubs() {
        int defaultNodeTime = 260000;
        chatList = new ChatList();
        chatList.setSuccess(true);
        chatList.setNodeTimestamp(defaultNodeTime);
        chatList.setChats(new ArrayList<>());
        chatList.setCount(0);

        transactionList = new TransactionList();
        transactionList.setSuccess(true);
        transactionList.setNodeTimestamp(defaultNodeTime);
        transactionList.setTransactions(new ArrayList<>());
        transactionList.setCount(0);

        messageList = new MessageList();
        messageList.setSuccess(true);
        messageList.setNodeTimestamp(defaultNodeTime);
        messageList.setMessages(new ArrayList<>());
        messageList.setCount(0);

        publicKeyResponse = new PublicKeyResponse();
        publicKeyResponse.setSuccess(true);
        publicKeyResponse.setNodeTimestamp(defaultNodeTime);

        transactionWasNormalized = new TransactionWasNormalized();
        transactionWasNormalized.setSuccess(true);
        transactionWasNormalized.setNodeTimestamp(defaultNodeTime);

        transactionWasProcessed = new TransactionWasProcessed();
        transactionWasProcessed.setSuccess(true);
        transactionWasProcessed.setNodeTimestamp(defaultNodeTime);

        operationComplete = new OperationComplete();
        operationComplete.setSuccess(true);
        operationComplete.setNodeTimestamp(defaultNodeTime);

        parametrizedTransactionList = new ParametrizedTransactionList();
        parametrizedTransactionList.setCount(0);
        parametrizedTransactionList.setSuccess(true);
        parametrizedTransactionList.setNodeTimestamp(defaultNodeTime);

        transactionDetailsResponse = new TransactionDetailsResponse();
        transactionDetailsResponse.setSuccess(true);
        transactionDetailsResponse.setNodeTimestamp(defaultNodeTime);

        Account account = new Account();
        account.setAddress(TestConstants.TEST_ADDRESS);
        account.setBalance(TestConstants.TEST_BALANCE);
        account.setUnconfirmedBalance(TestConstants.TEST_BALANCE);
        account.setPublicKey(TestConstants.TEST_PUBLIC_KEY);

        authorization = new Authorization();
        authorization.setAccount(account);
        authorization.setNodeTimestamp(defaultNodeTime);
        authorization.setSuccess(true);
    }
}
