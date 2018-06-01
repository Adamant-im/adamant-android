package com.dremanovich.adamant_android.interactors;

import com.dremanovich.adamant_android.core.AdamantApi;
import com.dremanovich.adamant_android.core.encryption.Encryptor;
import com.dremanovich.adamant_android.core.entities.Account;
import com.dremanovich.adamant_android.core.entities.Transaction;
import com.dremanovich.adamant_android.core.entities.UnnormalizedTransactionMessage;
import com.dremanovich.adamant_android.core.helpers.interfaces.AuthorizationStorage;
import com.dremanovich.adamant_android.core.helpers.interfaces.PublicKeyStorage;
import com.dremanovich.adamant_android.core.requests.ProcessTransaction;
import com.dremanovich.adamant_android.core.responses.TransactionWasProcessed;
import com.dremanovich.adamant_android.ui.entities.Chat;
import com.dremanovich.adamant_android.ui.entities.Message;
import com.dremanovich.adamant_android.ui.mappers.TransactionToMessageMapper;
import com.dremanovich.adamant_android.ui.mappers.TransactionToChatMapper;
import com.goterl.lazycode.lazysodium.utils.KeyPair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class ChatsInteractor {
    private AdamantApi api;
    private AuthorizationStorage authorizationStorage;
    private TransactionToChatMapper chatMapper;
    private TransactionToMessageMapper messageMapper;
    private Encryptor encryptor;
    private PublicKeyStorage publicKeyStorage;

    private int currentHeight = 1;

    //TODO: So far, the manipulation of the chat lists is entrusted to this interactor, but perhaps over time it's worth changing
    private HashMap<String, List<Message>> messagesByChats = new HashMap<>();
    private List<Chat> chats = new ArrayList<>();

    //TODO: Decrease the count of parameters
    public ChatsInteractor(
            AdamantApi api,
            AuthorizationStorage authorizationStorage,
            TransactionToMessageMapper messageMapper,
            TransactionToChatMapper chatMapper,
            Encryptor encryptor,
            PublicKeyStorage publicKeyStorage
    ) {
        this.api = api;
        this.authorizationStorage = authorizationStorage;
        this.chatMapper = chatMapper;
        this.messageMapper = messageMapper;
        this.encryptor = encryptor;
        this.publicKeyStorage = publicKeyStorage;
    }

    public Completable synchronizeWithBlockchain(){
        Account account = authorizationStorage.getAccount();

        if (account == null){
            return Completable.error(new Exception("You are not authorized."));
        }

        String address = account.getAddress();

        //TODO: Schedulers must be injected through Dagger for comfort unit-testing

        //TODO: The current height should be changed

        //TODO: Use database for save received transactions

        //TODO: It is necessary to implement periodic loading

         return Completable.fromObservable(
             api.getTransactions(address, currentHeight, AdamantApi.ORDER_BY_TIMESTAMP_ASC)
                 .subscribeOn(Schedulers.io())
                 .observeOn(Schedulers.computation())
                 .flatMap(transactionList -> {
                     if (transactionList.isSuccess()){
                         return Observable.fromIterable(transactionList.getTransactions());
                     } else {
                         return Observable.error(new Exception(transactionList.getError()));
                     }
                 })
                 .doOnNext(transaction -> {
                     Chat chat = chatMapper.apply(transaction);
                     if (!chats.contains(chat)){
                         chats.add(chat);
                     }
                 })
                 .doOnNext(transaction -> {
                     Message message = messageMapper.apply(transaction);
                     List<Message> messages = messagesByChats.get(message.getCompanionId());
                     if (messages != null) {
                         //If we sent this message and it's already in the list
                         if (!messages.contains(message)){
                             messages.add(message);
                         }
                     } else {
                         List<Message> newMessageBlock = new ArrayList<>();
                         newMessageBlock.add(message);
                         messagesByChats.put(message.getCompanionId(), newMessageBlock);
                     }
                 })
                 .doOnNext(transaction -> {
                     if (transaction.getHeight() > currentHeight) {
                         currentHeight =  transaction.getHeight();
                     }
                 })
                 .doOnComplete(() -> {
                     //Setting last message to chats
                     for(Chat chat : chats){
                         List<Message> messages = messagesByChats.get(chat.getCompanionId());
                         if (messages != null && messages.size() > 0){
                             Message mes = messages.get(messages.size() - 1);
                             if (mes != null){chat.setLastMessage(mes);}
                         }
                     }
                 })
         );
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

    public List<Chat> getChatList() {
        return chats;
    }

    public List<Message> getMessagesByCompanionId(String companionId) {
        List<Message> requestedMessages = messagesByChats.get(companionId);

        if (requestedMessages == null){return new ArrayList<>();}

        return requestedMessages;
    }

}
