package com.dremanovich.adamant_android.core;

import com.dremanovich.adamant_android.core.entities.Transaction;
import com.dremanovich.adamant_android.core.entities.UnnormalizedTransactionMessage;
import com.dremanovich.adamant_android.core.requests.ProcessTransaction;
import com.dremanovich.adamant_android.core.responses.Authorization;
import com.dremanovich.adamant_android.core.responses.PublicKeyResponse;
import com.dremanovich.adamant_android.core.responses.TransactionList;
import com.dremanovich.adamant_android.core.responses.TransactionWasNormalized;
import com.dremanovich.adamant_android.core.responses.TransactionWasProcessed;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AdamantApi {
    @GET("accounts")
    Observable<Authorization> authorize(@Query("publicKey") String publicKey);

    @GET("chats/get")
    Observable<TransactionList> getChats(@Query("isIn") String address, @Query("fromHeight") int height);

    @GET("accounts/getPublicKey")
    Observable<PublicKeyResponse> getPublicKey(@Query("address") String address);

    @POST("chats/normalize")
    Observable<TransactionWasNormalized> getNormalizedTransaction(@Body UnnormalizedTransactionMessage unnormalizedTransactionMessage);

    @POST("chats/process")
    Observable<TransactionWasProcessed> processTransaction(@Body ProcessTransaction transaction);
}
