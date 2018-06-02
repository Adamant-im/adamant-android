package com.dremanovich.adamant_android.core;

import com.dremanovich.adamant_android.core.entities.Transaction;
import com.dremanovich.adamant_android.core.entities.UnnormalizedTransactionMessage;
import com.dremanovich.adamant_android.core.requests.ProcessTransaction;
import com.dremanovich.adamant_android.core.responses.Authorization;
import com.dremanovich.adamant_android.core.responses.PublicKeyResponse;
import com.dremanovich.adamant_android.core.responses.TransactionList;
import com.dremanovich.adamant_android.core.responses.TransactionWasNormalized;
import com.dremanovich.adamant_android.core.responses.TransactionWasProcessed;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AdamantApi {
    long BASE_TIMESTAMP = 1504371600000L; //2017-08-02 17:00:00
    int SYNCHRONIZE_DELAY_SECONDS = 6;
    int MAX_TRANSACTIONS_PER_REQUEST = 100;
    String ORDER_BY_TIMESTAMP_DESC = "timestamp:desc";
    String ORDER_BY_TIMESTAMP_ASC = "timestamp:asc";

    @GET("accounts")
    Flowable<Authorization> authorize(@Query("publicKey") String publicKey);

    @GET("chats/get")
    Flowable<TransactionList> getTransactions(
            @Query("isIn") String address,
            @Query("fromHeight") int height,
            @Query("orderBy") String order
    );

    @GET("chats/get")
    Flowable<TransactionList> getTransactions(
            @Query("isIn") String address,
            @Query("orderBy") String order,
            @Query("offset") int offset
    );

    @GET("accounts/getPublicKey")
    Flowable<PublicKeyResponse> getPublicKey(@Query("address") String address);

    @POST("chats/normalize")
    Flowable<TransactionWasNormalized> getNormalizedTransaction(@Body UnnormalizedTransactionMessage unnormalizedTransactionMessage);

    @POST("chats/process")
    Flowable<TransactionWasProcessed> processTransaction(@Body ProcessTransaction transaction);
}
