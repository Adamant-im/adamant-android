package com.dremanovich.adamant_android.interactors;

import com.dremanovich.adamant_android.core.AdamantApi;
import com.dremanovich.adamant_android.core.entities.Account;
import com.dremanovich.adamant_android.core.helpers.interfaces.AuthorizationStorage;
import com.dremanovich.adamant_android.core.responses.Authorization;
import com.dremanovich.adamant_android.ui.entities.Chat;
import com.dremanovich.adamant_android.ui.mappers.TransactionsToChatsMapper;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ChatsInteractor {
    private AdamantApi api;
    private AuthorizationStorage authorizationStorage;
    private TransactionsToChatsMapper mapper;
    private int currentHeight = 1;

    public ChatsInteractor(
            AdamantApi api,
            AuthorizationStorage authorizationStorage,
            TransactionsToChatsMapper mapper
    ) {
        this.api = api;
        this.authorizationStorage = authorizationStorage;
        this.mapper = mapper;
    }

    public Observable<List<Chat>> loadChats(){
        Account account = authorizationStorage.getAccount();

        if (account == null){
            return Observable.error(new Exception("You are not authorized."));
        }

        String address = account.getAddress();

        //TODO: Schedulers must be injected through Dagger for comfort unit-testing

        //TODO: The current height should be changed

        //TODO: Use database for save received transactions

        //TODO: It is necessary to implement periodic loading

        return api.getChats(address, currentHeight)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(mapper)
                .observeOn(AndroidSchedulers.mainThread());

    }
}
