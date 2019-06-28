package im.adamant.android.core;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.goterl.lazycode.lazysodium.utils.KeyPair;

import java.io.IOException;

import im.adamant.android.BuildConfig;
import im.adamant.android.core.encryption.AdamantKeyGenerator;
import im.adamant.android.core.entities.Account;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.UnnormalizedTransactionMessage;
import im.adamant.android.core.entities.transaction_assets.NotUsedAsset;
import im.adamant.android.core.entities.transaction_assets.TransactionChatAsset;
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.core.entities.transaction_assets.TransactionStateAsset;
import im.adamant.android.core.requests.NewAccount;
import im.adamant.android.core.requests.ProcessTransaction;
import im.adamant.android.core.responses.Authorization;
import im.adamant.android.core.responses.ChatList;
import im.adamant.android.core.responses.MessageList;
import im.adamant.android.core.responses.OperationComplete;
import im.adamant.android.core.responses.ParametrizedTransactionList;
import im.adamant.android.core.responses.PublicKeyResponse;
import im.adamant.android.core.responses.TransactionList;
import im.adamant.android.core.responses.TransactionWasNormalized;
import im.adamant.android.core.responses.TransactionWasProcessed;
import io.reactivex.Completable;
import io.reactivex.Flowable;
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

    public AdamantApiWrapper(AdamantApiBuilder apiBuilder, AdamantKeyGenerator keyGenerator) {
        this.apiBuilder = apiBuilder;
        this.keyGenerator = keyGenerator;

        buildApi();
    }

    public Flowable<Authorization> authorize(CharSequence passPhrase) {
        this.passPhrase = passPhrase;
        KeyPair tempKeyPair = keyGenerator.getKeyPairFromPassPhrase(passPhrase);

        return authorize(tempKeyPair);
    }

    public Flowable<Authorization> authorize(KeyPair tempKeyPair) {
        return api
                .authorize(tempKeyPair.getPublicKeyString().toLowerCase())
                .subscribeOn(Schedulers.io())
                .doOnNext((authorization -> {
                    this.account = authorization.getAccount();
                    this.keyPair = tempKeyPair;
                }))
                .doOnError(this::checkNodeError)
                .doOnNext(authorization -> calcDeltas(authorization.getNodeTimestamp()))
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
    }

    public Completable updateBalance(){
        try {
            return api
                    .authorize(keyPair.getPublicKeyString().toLowerCase())
                    .subscribeOn(Schedulers.io())
                    .doOnNext((authorization -> {
                        if (authorization.getAccount() == null){return;}

                        if (this.account == null){
                            this.account = authorization.getAccount();
                            return;
                        }

                        this.account.setBalance(authorization.getAccount().getBalance());
                        this.account.setUnconfirmedBalance(authorization.getAccount().getUnconfirmedBalance());

                    }))
                    .doOnError(this::checkNodeError)
                    .doOnNext(authorization -> calcDeltas(authorization.getNodeTimestamp()))
                    .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}})
                    .ignoreElements();
        } catch (Exception ex){
            return Completable.error(ex);
        }


    }

    public Flowable<TransactionList> getMessageTransactionsByHeightAndOffset(int height, int offset, String order) {

        if (!isAuthorized()){return Flowable.error(new NotAuthorizedException("Not authorized"));}

        return api
                .getMessageTransactions(account.getAddress(), height, offset, order)
                .subscribeOn(Schedulers.io())
                .doOnError(this::checkNodeError)
                .doOnNext(transactionList -> calcDeltas(transactionList.getNodeTimestamp()))
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
    }


    public Flowable<ChatList> getChats(String order) {
        if (!isAuthorized()){return Flowable.error(new NotAuthorizedException("Not authorized"));}

        return api
                .getChats(account.getAddress(), order)
                .subscribeOn(Schedulers.io())
                .doOnError(this::checkNodeError)
                .doOnNext(chatList -> calcDeltas(chatList.getNodeTimestamp()))
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
    }

    //TODO: refactor duplicate code. May be use transformation
    public Flowable<ChatList> getChatsByOffset(int offset, String order) {
        if (!isAuthorized()){return Flowable.error(new NotAuthorizedException("Not authorized"));}

        return api
                .getChatsByOffset(account.getAddress(), offset, order)
                .subscribeOn(Schedulers.io())
                .doOnError(this::checkNodeError)
                .doOnNext(transactionList -> calcDeltas(transactionList.getNodeTimestamp()))
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
    }

    public Flowable<MessageList> getMessages(String companionAddress, String order) {
        if (!isAuthorized()){return Flowable.error(new NotAuthorizedException("Not authorized"));}

        return api
                .getMessages(account.getAddress(), companionAddress, order)
                .subscribeOn(Schedulers.io())
                .doOnError(this::checkNodeError)
                .doOnNext(chatList -> calcDeltas(chatList.getNodeTimestamp()))
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
    }

    public Flowable<MessageList> getMessagesByOffset(String companionAddress, int offset, String order) {
        if (!isAuthorized()){return Flowable.error(new NotAuthorizedException("Not authorized"));}

        return api
                .getMessagesByOffset(account.getAddress(), companionAddress, offset, order)
                .subscribeOn(Schedulers.io())
                .doOnError(this::checkNodeError)
                .doOnNext(transactionList -> calcDeltas(transactionList.getNodeTimestamp()))
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
    }

    public Flowable<PublicKeyResponse> getPublicKey(String address) {
        return api
                .getPublicKey(address)
                .subscribeOn(Schedulers.io())
                .doOnError(this::checkNodeError)
                .doOnNext(publicKeyResponse -> calcDeltas(publicKeyResponse.getNodeTimestamp()))
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
    }

    public Flowable<TransactionWasNormalized<TransactionChatAsset>> getNormalizedTransaction(UnnormalizedTransactionMessage unnormalizedTransactionMessage) {
        return api
                .getNormalizedTransaction(unnormalizedTransactionMessage)
                .subscribeOn(Schedulers.io())
                .doOnError(this::checkNodeError)
                .doOnNext(transactionWasNormalized -> calcDeltas(transactionWasNormalized.getNodeTimestamp()))
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
    }

    public Flowable<TransactionWasProcessed> processTransaction(ProcessTransaction transaction) {
        return api
                .processTransaction(transaction)
                .subscribeOn(Schedulers.io())
                .doOnError(this::checkNodeError)
                .doOnNext(transactionWasProcessed -> calcDeltas(transactionWasProcessed.getNodeTimestamp()))
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
    }

    public Flowable<Authorization> createNewAccount(CharSequence passPhrase) {
        KeyPair tempKeyPair = keyGenerator.getKeyPairFromPassPhrase(passPhrase);

        NewAccount newAccount = new NewAccount();
        newAccount.setPublicKey(tempKeyPair.getPublicKeyString().toLowerCase());

        return api.createNewAccount(newAccount)
                .subscribeOn(Schedulers.io())
                .doOnNext((authorization -> {
                    this.account = authorization.getAccount();
                    this.keyPair = tempKeyPair;
                }))
                .doOnError(this::checkNodeError)
                .doOnNext(authorization -> calcDeltas(authorization.getNodeTimestamp()))
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
    }

    public Flowable<OperationComplete> sendToKeyValueStorage(Transaction<TransactionStateAsset> transaction) {
        return api.sendToKeyValueStorage(new ProcessTransaction(transaction))
                .subscribeOn(Schedulers.io())
                .doOnError(this::checkNodeError)
                .doOnNext(operationComplete -> calcDeltas(operationComplete.getNodeTimestamp()))
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
    }

    public Flowable<ParametrizedTransactionList<TransactionStateAsset>> getFromKeyValueStorage(
            String senderId,
            String key,
            String order,
            int limit
    ) {
        return api.getFromKeyValueStorage(senderId, key, order, limit)
                .subscribeOn(Schedulers.io())
                .doOnError(this::checkNodeError)
                .doOnNext(operationComplete -> calcDeltas(operationComplete.getNodeTimestamp()))
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
    }

    public Flowable<TransactionList> getAdamantTransactions(int type, String order) {
        if (!isAuthorized()){return Flowable.error(new NotAuthorizedException("Not authorized"));}
        return api.getAdamantTransactions(account.getAddress(), type, 1, 0, order, AdamantApi.DEFAULT_TRANSACTIONS_LIMIT)
                .subscribeOn(Schedulers.io())
                .doOnError(this::checkNodeError)
                .doOnNext(operationComplete -> calcDeltas(operationComplete.getNodeTimestamp()))
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
    }

    public Flowable<TransactionList> getAdamantAllFinanceTransactions(String order) {
        if (!isAuthorized()){return Flowable.error(new NotAuthorizedException("Not authorized"));}
        return api.getAdamantAllFinanceTransactions(account.getAddress(),1, 1, 0, order, AdamantApi.DEFAULT_TRANSACTIONS_LIMIT)
                .subscribeOn(Schedulers.io())
                .doOnError(this::checkNodeError)
                .doOnNext(operationComplete -> calcDeltas(operationComplete.getNodeTimestamp()))
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
    }

    public Flowable<TransactionList> getAdamantAllFinanceTransactions(int type, int fromHeight, int offset, String order) {
        if (!isAuthorized()){return Flowable.error(new NotAuthorizedException("Not authorized"));}
        return api.getAdamantAllFinanceTransactions(account.getAddress(), fromHeight, 1, offset, order, AdamantApi.DEFAULT_TRANSACTIONS_LIMIT)
                .subscribeOn(Schedulers.io())
                .doOnError(this::checkNodeError)
                .doOnNext(operationComplete -> calcDeltas(operationComplete.getNodeTimestamp()))
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
    }

    public Flowable<TransactionList> getAdamantAllFinanceTransactions(int fromHeight, int offset, String order) {
        if (!isAuthorized()){return Flowable.error(new NotAuthorizedException("Not authorized"));}
        return api.getAdamantAllFinanceTransactions(account.getAddress(), fromHeight, 1, offset, order, AdamantApi.DEFAULT_TRANSACTIONS_LIMIT)
                .subscribeOn(Schedulers.io())
                .doOnError(this::checkNodeError)
                .doOnNext(operationComplete -> calcDeltas(operationComplete.getNodeTimestamp()))
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
    }

    public Flowable<TransactionWasProcessed> sendAdmTransferTransaction(ProcessTransaction transaction) {
        return api.sendAdmTransferTransaction(transaction)
                .subscribeOn(Schedulers.io())
                .doOnError(this::checkNodeError)
                .doOnNext(operationComplete -> calcDeltas(operationComplete.getNodeTimestamp()))
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
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

            if (errorsCount > BuildConfig.MAX_ERRORS_FOR_CHANGE_NODE){
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
}
