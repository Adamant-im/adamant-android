package im.adamant.android.core;

import com.goterl.lazycode.lazysodium.utils.KeyPair;

import java.io.IOException;

import im.adamant.android.BuildConfig;
import im.adamant.android.core.encryption.KeyGenerator;
import im.adamant.android.core.entities.Account;
import im.adamant.android.core.entities.UnnormalizedTransactionMessage;
import im.adamant.android.core.requests.NewAccount;
import im.adamant.android.core.requests.ProcessTransaction;
import im.adamant.android.core.responses.Authorization;
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
    private AdamantApiBuilder apiBuilder;
    private KeyPair keyPair;
    private Account account;
    private KeyGenerator keyGenerator;

    private Disposable wrapperBuildSubscription;

    private int errorsCount;

    public AdamantApiWrapper(AdamantApiBuilder apiBuilder, KeyGenerator keyGenerator) {
        this.apiBuilder = apiBuilder;
        this.keyGenerator = keyGenerator;

        buildApi();
    }

    public Flowable<Authorization> authorize(String passPhrase) {
        KeyPair tempKeyPair = keyGenerator.getKeyPairFromPassPhrase(passPhrase);

        return api
                .authorize(tempKeyPair.getPublicKeyString().toLowerCase())
                .subscribeOn(Schedulers.io())
                .doOnNext((authorization -> {
                    this.account = authorization.getAccount();
                    this.keyPair = tempKeyPair;
                }))
                .doOnError(this::checkNodeError)
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
                    .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}})
                    .ignoreElements();
        } catch (Exception ex){
            return Completable.error(ex);
        }


    }

    public Flowable<TransactionList> getTransactions(int height, String order) {

        if (!isAuthorized()){return Flowable.error(new Exception("Not authorized"));}

        return api
                .getTransactions(account.getAddress(), height, order)
                .subscribeOn(Schedulers.io())
                .doOnError(this::checkNodeError)
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
    }

    public Flowable<TransactionList> getTransactions(String order, int offset) {

        if (!isAuthorized()){return Flowable.error(new Exception("Not authorized"));}

        return api
                .getTransactions(account.getAddress(), order, offset)
                .subscribeOn(Schedulers.io())
                .doOnError(this::checkNodeError)
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
    }

    public Flowable<PublicKeyResponse> getPublicKey(String address) {
        return api
                .getPublicKey(address)
                .subscribeOn(Schedulers.io())
                .doOnError(this::checkNodeError)
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
    }

    public Flowable<TransactionWasNormalized> getNormalizedTransaction(UnnormalizedTransactionMessage unnormalizedTransactionMessage) {
        return api
                .getNormalizedTransaction(unnormalizedTransactionMessage)
                .subscribeOn(Schedulers.io())
                .doOnError(this::checkNodeError)
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
    }

    public Flowable<TransactionWasProcessed> processTransaction(ProcessTransaction transaction) {
        return api
                .processTransaction(transaction)
                .subscribeOn(Schedulers.io())
                .doOnError(this::checkNodeError)
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
    }

    public Flowable<Authorization> createNewAccount(String passPhrase) {
        KeyPair tempKeyPair = keyGenerator.getKeyPairFromPassPhrase(passPhrase);

        NewAccount newAccount = new NewAccount();
        newAccount.setPublicKey(keyPair.getPublicKeyString().toLowerCase());

        return api.createNewAccount(newAccount)
                .subscribeOn(Schedulers.io())
                .doOnNext((authorization -> {
                    this.account = authorization.getAccount();
                    this.keyPair = tempKeyPair;
                }))
                .doOnError(this::checkNodeError)
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
    }

    public boolean isAuthorized() {
        return account != null && keyPair != null;
    }

    public void logout() {
        account = null;
        keyPair = null;
    }

    public Account getAccount() {
        return account;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    private void buildApi() {

        if (wrapperBuildSubscription != null){
            wrapperBuildSubscription.dispose();
        }

        wrapperBuildSubscription = apiBuilder.build()
                .doOnNext(buildedApi -> api = buildedApi)
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
}
