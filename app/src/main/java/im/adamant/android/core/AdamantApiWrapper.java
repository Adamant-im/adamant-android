package im.adamant.android.core;


import com.goterl.lazycode.lazysodium.utils.KeyPair;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.Transformer;

import im.adamant.android.BuildConfig;
import im.adamant.android.core.encryption.AdamantKeyGenerator;
import im.adamant.android.core.entities.Account;
import im.adamant.android.core.entities.HasNodeTimestamp;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.UnnormalizedTransactionMessage;
import im.adamant.android.core.entities.transaction_assets.TransactionChatAsset;
import im.adamant.android.core.entities.transaction_assets.TransactionStateAsset;
import im.adamant.android.core.exceptions.NotAuthorizedException;
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
import im.adamant.android.helpers.LoggerHelper;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;


public class AdamantApiWrapper {
    private AdamantApi api;
    private KeyPair keyPair;
    private Account account;
    private CharSequence passPhrase;
    private AdamantKeyGenerator keyGenerator;
    private AdamantApiBuilder apiBuilder;

    private Disposable wrapperBuildSubscription;

    private volatile int serverTimeDelta;
    private int errorsCount;

    private Scheduler scheduler;

    public AdamantApiWrapper(AdamantApiBuilder apiBuilder, AdamantKeyGenerator keyGenerator, Scheduler scheduler) {
        this.apiBuilder = apiBuilder;
        this.keyGenerator = keyGenerator;
        this.scheduler = scheduler;

        buildApi();
    }

    public Flowable<Authorization> authorize(CharSequence passPhrase) {
        this.passPhrase = passPhrase;
        KeyPair tempKeyPair = keyGenerator.getKeyPairFromPassPhrase(passPhrase);

        return authorize(tempKeyPair);
    }

    public Flowable<Authorization> authorize(KeyPair tempKeyPair) {
        return Flowable.defer(() -> api.authorize(tempKeyPair.getPublicKeyString().toLowerCase()))
                .doOnNext((authorization -> {
                    this.account = authorization.getAccount();
                    this.keyPair = tempKeyPair;
                }))
                .compose(requestControl());
    }

    public void setAuthorization(Account account, KeyPair keyPair) {
        this.account = account;
        this.keyPair = keyPair;
    }

    public Completable updateBalance(){
        try {
            return Flowable.defer(() -> api.authorize(keyPair.getPublicKeyString().toLowerCase()))
                    .doOnNext((authorization -> {
                        if (authorization.getAccount() == null){return;}

                        if (this.account == null){
                            this.account = authorization.getAccount();
                            return;
                        }

                        this.account.setBalance(authorization.getAccount().getBalance());
                        this.account.setUnconfirmedBalance(authorization.getAccount().getUnconfirmedBalance());

                    }))
                    .compose(requestControl())
                    .ignoreElements();
        } catch (Exception ex){
            return Completable.error(ex);
        }
    }

    public Flowable<TransactionList> getMessageTransactionsByHeightAndOffset(int height, int offset, String order) {

        if (!isAuthorized()){return Flowable.error(new NotAuthorizedException("Not authorized"));}

        return Flowable.defer(() -> api.getMessageTransactions(account.getAddress(), height, offset, order))
                .compose(requestControl())
                .compose(retryPolitics());
    }


    public Flowable<ChatList> getChats(String order) {
        if (!isAuthorized()){return Flowable.error(new NotAuthorizedException("Not authorized"));}

        return Flowable.defer(() -> api.getChats(account.getAddress(), order))
                .compose(requestControl())
                .compose(retryPolitics());
    }

    //TODO: refactor duplicate code. May be use transformation
    public Flowable<ChatList> getChatsByOffset(int offset, String order) {
        return getChatsByOffset(offset,AdamantApi.DEFAULT_TRANSACTIONS_LIMIT, order);
    }

    public Flowable<ChatList> getChatsByOffset(int offset,int limit, String order) {
        if (!isAuthorized()){return Flowable.error(new NotAuthorizedException("Not authorized"));}

        return Flowable.defer(() -> api.getChatsByOffset(account.getAddress(), offset,limit, order))
                .compose(requestControl())
                .compose(retryPolitics());
    }

    public Flowable<MessageList> getMessages(String companionAddress, String order) {
        if (!isAuthorized()){return Flowable.error(new NotAuthorizedException("Not authorized"));}

        return Flowable.defer(() -> api.getMessages(account.getAddress(), companionAddress, order))
                .compose(requestControl())
                .compose(retryPolitics());
    }

    public Flowable<MessageList> getMessagesByOffset(String companionAddress, int offset, String order) {
        return getMessagesByOffset(companionAddress, offset,
                AdamantApi.DEFAULT_TRANSACTIONS_LIMIT, order);
    }


    public Flowable<MessageList> getMessagesByOffset(String companionAddress, int offset,int limit, String order) {
        if (!isAuthorized()){return Flowable.error(new NotAuthorizedException("Not authorized"));}

        return Flowable.defer(() -> api.getMessagesByOffset(account.getAddress(), companionAddress, offset, limit, order))
                .compose(requestControl())
                .compose(retryPolitics());
    }

    public Flowable<PublicKeyResponse> getPublicKey(String address) {
        return Flowable.defer(() -> api.getPublicKey(address))
                .compose(requestControl())
                .compose(retryPolitics());
    }

    //TODO: Remove normalization procedure
    public Flowable<TransactionWasNormalized<TransactionChatAsset>> getNormalizedTransaction(UnnormalizedTransactionMessage unnormalizedTransactionMessage) {
        return Flowable.defer(() -> api.getNormalizedTransaction(unnormalizedTransactionMessage))
                .compose(requestControl());
    }

    public Flowable<TransactionWasProcessed> processTransaction(ProcessTransaction transaction) {
        return Flowable.defer(() -> api.processTransaction(transaction))
                .compose(requestControl());
    }

    public Flowable<Authorization> createNewAccount(CharSequence passPhrase) {
        KeyPair tempKeyPair = keyGenerator.getKeyPairFromPassPhrase(passPhrase);

        NewAccount newAccount = new NewAccount();
        newAccount.setPublicKey(tempKeyPair.getPublicKeyString().toLowerCase());

        return Flowable.defer(() -> api.createNewAccount(newAccount))
                .doOnNext((authorization -> {
                    this.account = authorization.getAccount();
                    this.keyPair = tempKeyPair;
                }))
                .compose(requestControl());
    }

    public Flowable<OperationComplete> sendToKeyValueStorage(Transaction<TransactionStateAsset> transaction) {
        return Flowable.defer(() -> api.sendToKeyValueStorage(new ProcessTransaction(transaction)))
                .compose(requestControl());
    }

    public Flowable<ParametrizedTransactionList<TransactionStateAsset>> getFromKeyValueStorage(
            String senderId,
            String key,
            String order,
            int limit
    ) {
        return Flowable.defer(() -> api.getFromKeyValueStorage(senderId, key, order, limit))
                .compose(requestControl())
                .compose(retryPolitics());
    }

    public Flowable<TransactionList> getAdamantTransactions(int type, String order) {
        if (!isAuthorized()){return Flowable.error(new NotAuthorizedException("Not authorized"));}

        return Flowable.defer(() -> api.getAdamantTransactions(account.getAddress(), type, 1, 0, order, AdamantApi.DEFAULT_TRANSACTIONS_LIMIT))
                .compose(requestControl())
                .compose(retryPolitics());
    }

    public Flowable<TransactionList> getAdamantAllFinanceTransactions(String order) {
        if (!isAuthorized()){return Flowable.error(new NotAuthorizedException("Not authorized"));}

        return Flowable.defer(() -> api.getAdamantAllFinanceTransactions(account.getAddress(),1, 1, 0, order, AdamantApi.DEFAULT_TRANSACTIONS_LIMIT))
                .compose(requestControl())
                .compose(retryPolitics());
    }

    public Flowable<TransactionList> getAdamantAllFinanceTransactions(int type, int fromHeight, int offset, String order) {
        if (!isAuthorized()){return Flowable.error(new NotAuthorizedException("Not authorized"));}

        return Flowable.defer(() -> api.getAdamantAllFinanceTransactions(account.getAddress(), fromHeight, 1, offset, order, AdamantApi.DEFAULT_TRANSACTIONS_LIMIT))
                .compose(requestControl())
                .compose(retryPolitics());
    }

    public Flowable<TransactionList> getAdamantAllFinanceTransactions(int fromHeight, int offset, String order) {
        if (!isAuthorized()){return Flowable.error(new NotAuthorizedException("Not authorized"));}

        return Flowable.defer(() -> api.getAdamantAllFinanceTransactions(account.getAddress(), fromHeight, 1, offset, order, AdamantApi.DEFAULT_TRANSACTIONS_LIMIT))
                .compose(requestControl())
                .compose(retryPolitics());
    }

    public Flowable<TransactionWasProcessed> sendAdmTransferTransaction(ProcessTransaction transaction) {
        return Flowable.defer(() -> api.sendAdmTransferTransaction(transaction))
                .compose(requestControl());
    }

    public Flowable<TransactionDetailsResponse> getTransactionDetails(String transactionId){
        return Flowable.defer(() -> api.getTransactionDetails(transactionId))
                .compose(requestControl())
                .compose(retryPolitics());
    }

    public boolean isAuthorized() {
        return account != null && keyPair != null;
    }

    public void logout() {
        account = null;
        keyPair = null;
        errorsCount = 0;
    }

    public Account getAccount() {
        return account;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public CharSequence getPassPhrase() {
        return passPhrase;
    }

    public void buildApibyIndex(int index) {
        if (wrapperBuildSubscription != null){
            wrapperBuildSubscription.dispose();
        }

        wrapperBuildSubscription = apiBuilder.build(index)
                .doOnNext(buildedApi -> api = buildedApi)
                .doOnError(Throwable::printStackTrace)
                .retry(1000)
                .subscribe();
    }

    private void buildApi() {

        if (wrapperBuildSubscription != null){
            wrapperBuildSubscription.dispose();
        }

        wrapperBuildSubscription = apiBuilder.build()
                .doOnNext(buildedApi -> api = buildedApi)
                .doOnError(Throwable::printStackTrace)
                .retry(1000)
                .subscribe();

    }

    private void checkNodeError(Throwable e){
        if ((e instanceof IOException) || (e instanceof HttpException)){
            errorsCount++;

            LoggerHelper.e(getClass().getName(), e.getMessage(), e);

            if (errorsCount >= BuildConfig.MAX_ERRORS_FOR_CHANGE_NODE){
                errorsCount = 0;
                buildApi();
            }
        }
    }

    private synchronized void calcDeltas(int timestamp) {
        serverTimeDelta = getEpoch() - timestamp;
    }

    public synchronized int getServerTimeDelta() {
        return serverTimeDelta;
    }

    public int getEpoch() {
        return (int) ((System.currentTimeMillis() - AdamantApi.BASE_TIMESTAMP) / 1000);
    }

    private <T extends HasNodeTimestamp> FlowableTransformer<T, T> requestControl() {
        return observable -> observable
                .subscribeOn(scheduler)
                .doOnError(this::checkNodeError)
                .doOnNext(operationComplete -> calcDeltas(operationComplete.getNodeTimestamp()))
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
    }

    private <T> FlowableTransformer<T, T> retryPolitics() {
        return observable -> observable
                .retryWhen(throwableFlowable -> throwableFlowable.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS, TimeUnit.SECONDS, scheduler));
    }
}