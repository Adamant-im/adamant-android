package im.adamant.android.interactors;

import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.encryption.Encryptor;
import im.adamant.android.core.entities.Account;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.UnnormalizedTransactionMessage;
import im.adamant.android.core.helpers.interfaces.AuthorizationStorage;
import im.adamant.android.core.helpers.interfaces.PublicKeyStorage;
import im.adamant.android.core.requests.ProcessTransaction;
import im.adamant.android.core.responses.TransactionList;
import im.adamant.android.core.responses.TransactionWasProcessed;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.entities.Message;
import im.adamant.android.ui.mappers.LocalizedMessageMapper;
import im.adamant.android.ui.mappers.TransactionToMessageMapper;
import im.adamant.android.ui.mappers.TransactionToChatMapper;
import com.goterl.lazycode.lazysodium.utils.KeyPair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class ChatsInteractor {
    private AdamantApi api;
    private AuthorizationStorage authorizationStorage;
    private TransactionToChatMapper chatMapper;
    private TransactionToMessageMapper messageMapper;
    private LocalizedMessageMapper localizedMessageMapper;
    private Encryptor encryptor;
    private PublicKeyStorage publicKeyStorage;

    private int countItems = 0;
    private int currentHeight = 1;
    private int offsetItems = 0;

    //TODO: So far, the manipulation of the chat lists is entrusted to this interactor, but perhaps over time it's worth changing
    //TODO: Multithreaded access to properties can cause problems in the future
    private HashMap<String, List<Message>> messagesByChats = new HashMap<>();
    private List<Chat> chats = new ArrayList<>();

    //TODO: Decrease the count of parameters
    public ChatsInteractor(
            AdamantApi api,
            AuthorizationStorage authorizationStorage,
            TransactionToMessageMapper messageMapper,
            TransactionToChatMapper chatMapper,
            LocalizedMessageMapper localizedMessageMapper,
            Encryptor encryptor,
            PublicKeyStorage publicKeyStorage
    ) {
        this.api = api;
        this.authorizationStorage = authorizationStorage;
        this.chatMapper = chatMapper;
        this.messageMapper = messageMapper;
        this.encryptor = encryptor;
        this.publicKeyStorage = publicKeyStorage;
        this.localizedMessageMapper = localizedMessageMapper;
    }

    //TODO: Refactor this. Too long method

    public Completable synchronizeWithBlockchain(){
        Account account = authorizationStorage.getAccount();

        if (account == null){
            return Completable.error(new Exception("You are not authorized."));
        }

        String address = account.getAddress();

        //TODO: Schedulers must be injected through Dagger for comfort unit-testing

        //TODO: The current height should be "Atomic" changed

        //TODO: Use database for save received transactions

          return Flowable
                 .defer(() -> Flowable.just(currentHeight))
                 .flatMap((height) -> {
                     Flowable<TransactionList> transactionFlowable = null;
                     if (offsetItems > 0){
                         transactionFlowable = api.getTransactions(address, AdamantApi.ORDER_BY_TIMESTAMP_ASC, offsetItems);
                     } else {
                         transactionFlowable = api.getTransactions(address, height, AdamantApi.ORDER_BY_TIMESTAMP_ASC);
                     }

                     return transactionFlowable.subscribeOn(Schedulers.io())
                             .observeOn(Schedulers.computation())
                             .flatMap(transactionList -> {
                                 if (transactionList.isSuccess()){
                                     return Flowable.fromIterable(transactionList.getTransactions());
                                 } else {
                                     return Flowable.error(new Exception(transactionList.getError()));
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
                                 message = localizedMessageMapper.apply(message);
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
                                 countItems++;
                                 if (transaction.getHeight() > currentHeight) {
                                     currentHeight = transaction.getHeight();
                                 }
                             })
                             .doOnError(Throwable::printStackTrace)
                             .doOnComplete(() -> {
                                 //Setting last message to chats
                                 for(Chat chat : chats){
                                     List<Message> messages = messagesByChats.get(chat.getCompanionId());
                                     if (messages != null && messages.size() > 0){
                                         Message mes = messages.get(messages.size() - 1);
                                         if (mes != null){chat.setLastMessage(mes);}
                                     }
                                 }
                             });
                 })
                  .repeatUntil(() -> {
                      boolean noRepeat = countItems < AdamantApi.MAX_TRANSACTIONS_PER_REQUEST;
                      if (noRepeat){
                          countItems = 0;
                          offsetItems = 0;
                      } else {
                          offsetItems += countItems;
                          countItems = 0;

                      }
                      return  noRepeat;
                  })
                  .ignoreElements();

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
                .flatMap((unnormalizedTransactionMessage -> Single.fromPublisher(
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
                .flatMap(transaction -> Single.fromPublisher(
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

    public void addNewChat(Chat chat) {
        if (chats.indexOf(chat) == -1){
            chats.add(chat);
            messagesByChats.put(chat.getCompanionId(), new ArrayList<>());
        }
    }
}
