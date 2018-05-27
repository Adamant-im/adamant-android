package com.dremanovich.adamant_android.core;

import com.dremanovich.adamant_android.core.responses.Authorization;
import com.dremanovich.adamant_android.core.responses.PublicKeyResponse;
import com.dremanovich.adamant_android.core.responses.TransactionList;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AdamantApi {
    @GET("accounts")
    Observable<Authorization> authorize(@Query("publicKey") String publicKey);

    @GET("chats/get")
    Observable<TransactionList> getChats(@Query("isIn") String address, @Query("fromHeight") int height);

    @GET("accounts/getPublicKey")
    Observable<PublicKeyResponse> getPublicKey(@Query("address") String address);
}
