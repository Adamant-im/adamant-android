package im.adamant.android.core;

import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.TransactionState;
import im.adamant.android.core.entities.UnnormalizedTransactionMessage;
import im.adamant.android.core.entities.transaction_assets.NotUsedAsset;
import im.adamant.android.core.entities.transaction_assets.TransactionChatAsset;
import im.adamant.android.core.entities.transaction_assets.TransactionStateAsset;
import im.adamant.android.core.requests.NewAccount;
import im.adamant.android.core.requests.ProcessTransaction;
import im.adamant.android.core.responses.Authorization;
import im.adamant.android.core.responses.OperationComplete;
import im.adamant.android.core.responses.PublicKeyResponse;
import im.adamant.android.core.responses.TransactionList;
import im.adamant.android.core.responses.TransactionWasNormalized;
import im.adamant.android.core.responses.TransactionWasProcessed;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AdamantApi {
    long MINIMUM_COST = 100_000L;
    long BASE_TIMESTAMP = 1504371600000L; //2017-08-02 17:00:00
    int SYNCHRONIZE_DELAY_SECONDS = 6;
    int MAX_TRANSACTIONS_PER_REQUEST = 100;
    String ORDER_BY_TIMESTAMP_DESC = "timestamp:desc";
    String ORDER_BY_TIMESTAMP_ASC = "timestamp:asc";

    @GET("accounts")
    Flowable<Authorization> authorize(@Query("publicKey") String publicKey);

    @GET("chats/get")
    Flowable<TransactionList<TransactionChatAsset>> getMessageTransactions(
            @Query("isIn") String address,
            @Query("fromHeight") int height,
            @Query("orderBy") String order
    );

    @GET("chats/get")
    Flowable<TransactionList<TransactionChatAsset>> getMessageTransactions(
            @Query("isIn") String address,
            @Query("orderBy") String order,
            @Query("offset") int offset
    );

    @GET("accounts/getPublicKey")
    Flowable<PublicKeyResponse> getPublicKey(@Query("address") String address);

    @POST("chats/normalize")
    Flowable<TransactionWasNormalized<TransactionChatAsset>> getNormalizedTransaction(@Body UnnormalizedTransactionMessage unnormalizedTransactionMessage);

    @POST("chats/process")
    Flowable<TransactionWasProcessed> processTransaction(@Body ProcessTransaction transaction);

    @POST("accounts/new")
    Flowable<Authorization> createNewAccount(@Body NewAccount accountKey);

    @POST("states/store")
    Flowable<OperationComplete> sendToKeyValueStorage(@Body ProcessTransaction transaction);

    @GET("states/get")
    Flowable<TransactionList<TransactionStateAsset>> getFromKeyValueStorage(
            @Query("senderId") String senderId,
            @Query("key") String key,
            @Query("orderBy") String order,
            @Query("limit") int limit
    );

    @GET("transactions")
    Flowable<TransactionList<NotUsedAsset>> getAdamantTransactions(
            @Query("inId") String address,
            @Query("and:type") int type,
            @Query("and:fromHeight") int height,
            @Query("orderBy") String order
    );
}
