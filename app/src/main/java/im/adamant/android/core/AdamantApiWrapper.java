package im.adamant.android.core;

import com.goterl.lazycode.lazysodium.utils.KeyPair;

import java.io.IOException;
import java.util.List;

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
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdamantApiWrapper {
    private AdamantApi api;
    private List<String> nodes;
    private KeyPair keyPair;
    private Account account;
    private String passPhrase;
    private KeyGenerator keyGenerator;

    private int errorsCount;

    public AdamantApiWrapper(List<String> nodes, KeyGenerator keyGenerator) {
        this.nodes = nodes;
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
                    this.passPhrase = passPhrase;
                }))
                .doOnError(this::checkNodeError)
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
    }

    public Flowable<TransactionList> getTransactions(int height, String order) {

        if (!isAuthorized()){return Flowable.error(new Exception("Not authorized"));}

        return api
                .getTransactions(account.getAddress(), height, order)
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext((error) -> {
                    checkNodeError(error);
                    return Flowable.just(new TransactionList());
                })
                .retry()
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
    }

    public Flowable<TransactionList> getTransactions(String order, int offset) {

        if (!isAuthorized()){return Flowable.error(new Exception("Not authorized"));}

        return api
                .getTransactions(account.getAddress(), order, offset)
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext((error) -> {
                    checkNodeError(error);
                    return Flowable.just(new TransactionList());
                })
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
    }

    public Flowable<PublicKeyResponse> getPublicKey(String address) {
        return api
                .getPublicKey(address)
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext((error) -> {
                    checkNodeError(error);
                    return Flowable.just(new PublicKeyResponse());
                })
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
    }

    public Flowable<TransactionWasNormalized> getNormalizedTransaction(UnnormalizedTransactionMessage unnormalizedTransactionMessage) {
        return api
                .getNormalizedTransaction(unnormalizedTransactionMessage)
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext((error) -> {
                    checkNodeError(error);
                    return Flowable.just(new TransactionWasNormalized());
                })
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
    }

    public Flowable<TransactionWasProcessed> processTransaction(ProcessTransaction transaction) {
        return api
                .processTransaction(transaction)
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext((error) -> {
                    checkNodeError(error);
                    return Flowable.just(new TransactionWasProcessed());
                })
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
                    this.passPhrase = passPhrase;
                }))
                .onErrorResumeNext((error) -> {
                    checkNodeError(error);
                    return Flowable.just(new Authorization());
                })
                .doOnNext((i) -> {if(errorsCount > 0) {errorsCount--;}});
    }

    public boolean isAuthorized() {
        return account != null && keyPair != null;
    }

    public void logout() {
        account = null;
        keyPair = null;
    }

    public String getPassPhrase() {
        return passPhrase;
    }

    public Account getAccount() {
        return account;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    private void buildApi() {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG){
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverSelect())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient.build())
                .build();

        api = retrofit.create(AdamantApi.class);
    }

    private String serverSelect() {
        int index =  (int) Math.round(Math.floor(Math.random() * nodes.size()));
        if (index >= nodes.size()){index = nodes.size() - 1;}

        return nodes.get(index);
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
