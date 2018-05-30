package com.dremanovich.adamant_android.interactors;

import android.util.Log;

import com.dremanovich.adamant_android.core.AdamantApi;
import com.dremanovich.adamant_android.core.encryption.Encryptor;
import com.dremanovich.adamant_android.core.entities.Account;
import com.dremanovich.adamant_android.core.entities.Transaction;
import com.dremanovich.adamant_android.core.entities.TransactionAsset;
import com.dremanovich.adamant_android.core.entities.TransactionMessage;
import com.dremanovich.adamant_android.core.entities.UnnormalizedTransactionMessage;
import com.dremanovich.adamant_android.core.helpers.interfaces.AuthorizationStorage;
import com.dremanovich.adamant_android.core.helpers.interfaces.PublicKeyStorage;
import com.dremanovich.adamant_android.core.requests.ProcessTransaction;
import com.dremanovich.adamant_android.core.responses.TransactionWasProcessed;
import com.dremanovich.adamant_android.ui.entities.Chat;
import com.dremanovich.adamant_android.ui.mappers.TransactionsToChatsMapper;
import com.goterl.lazycode.lazysodium.utils.KeyPair;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ChatsInteractor {
    private AdamantApi api;
    private AuthorizationStorage authorizationStorage;
    private TransactionsToChatsMapper mapper;
    private Encryptor encryptor;
    private PublicKeyStorage publicKeyStorage;

    private int currentHeight = 1;

    //TODO: Decrease the count of parameters
    public ChatsInteractor(
            AdamantApi api,
            AuthorizationStorage authorizationStorage,
            TransactionsToChatsMapper mapper,
            Encryptor encryptor,
            PublicKeyStorage publicKeyStorage
    ) {
        this.api = api;
        this.authorizationStorage = authorizationStorage;
        this.mapper = mapper;
        this.encryptor = encryptor;
        this.publicKeyStorage = publicKeyStorage;
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

    public Single<TransactionWasProcessed> sendMessage(String message, String address){
        KeyPair keyPair = authorizationStorage.getKeyPair();
        Account account = authorizationStorage.getAccount();

        if (keyPair == null || account == null){
            return Single.error(new Exception("You are not authorized."));
        }

        return Single
                .fromCallable(() -> publicKeyStorage.getPublicKey(address))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .flatMap((publicKey) -> Single.just(encryptor.encryptMessage(
                        message,
                        publicKey,
                        keyPair.getSecretKeyString().toLowerCase()
                )))
                .flatMap((transactionMessage -> Single.fromCallable(
                        () -> {
                            UnnormalizedTransactionMessage unnormalizedMessage = new UnnormalizedTransactionMessage();
                            unnormalizedMessage.setMessage(transactionMessage.getMessage());
                            unnormalizedMessage.setOwnMessage(transactionMessage.getOwnMessage());
                            unnormalizedMessage.setMessageType(1);
                            unnormalizedMessage.setType(8);
                            unnormalizedMessage.setPublicKey(keyPair.getPublicKeyString().toLowerCase());
                            unnormalizedMessage.setRecipientId(address);
                            unnormalizedMessage.setSenderId(account.getAddress());

                            return unnormalizedMessage;
                        }
                )))
                .flatMap((unnormalizedTransactionMessage -> Single.fromObservable(
                        api.getNormalizedTransaction(unnormalizedTransactionMessage)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.computation())
                )))
                .flatMap((transactionWasNormalized -> {
                    if (transactionWasNormalized.isSuccess()) {
                        Transaction transaction = transactionWasNormalized.getTransaction();
                        transaction.setSenderId(account.getAddress());

                        transaction.setSignature(
                                encryptor.createTransactionSignature(
                                        transaction,
                                        keyPair
                                )
                        );

                        return Single.just(transaction);
                    } else {
                        throw new Exception(transactionWasNormalized.getError());
                    }
                }))
                .flatMap(transaction -> Single.fromObservable(
                        api.processTransaction(new ProcessTransaction(transaction))
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.computation())
                ));
    }
}
