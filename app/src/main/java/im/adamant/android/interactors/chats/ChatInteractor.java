package im.adamant.android.interactors.chats;

import android.util.Pair;

import androidx.annotation.MainThread;

import java.io.IOException;
import java.util.List;

import im.adamant.android.core.exceptions.MessageDecryptException;
import im.adamant.android.core.responses.ChatList;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.helpers.PublicKeyStorage;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.mappers.TransactionToChatMapper;
import im.adamant.android.ui.mappers.TransactionToMessageMapper;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import im.adamant.android.ui.messages_support.entities.FallbackMessage;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ChatInteractor {
    //TODO: Schedulers must be injected through Dagger for comfort unit-testing

    //TODO: The current height should be "Atomic" changed

    //TODO: Use database for save received transactions

    //TODO: Well test the erroneous execution path, replace where you need doOnError

    private PublicKeyStorage keyStorage;
    private NewTransactionsSource newItemsSource;
    private LastTransactionInChatsSource chatsSource;
    private HistoryTransactionsSource historySource;
    private ContactsSource contactsSource;
    private ChatsStorage chatsStorage;
    private TransactionToChatMapper chatMapper;
    private TransactionToMessageMapper messageMapper;

    private int maxHeight = 1;

    public ChatInteractor(
            PublicKeyStorage keyStorage,
            NewTransactionsSource newItemsSource,
            LastTransactionInChatsSource chatsSource,
            HistoryTransactionsSource historySource,
            ContactsSource contactsSource,
            ChatsStorage chatsStorage,
            TransactionToChatMapper chatMapper,
            TransactionToMessageMapper messageMapper
    ) {
        this.keyStorage = keyStorage;
        this.newItemsSource = newItemsSource;
        this.chatsSource = chatsSource;
        this.historySource = historySource;
        this.contactsSource = contactsSource;
        this.chatsStorage = chatsStorage;
        this.chatMapper = chatMapper;
        this.messageMapper = messageMapper;
    }

    public static final int PAGE_SIZE = 25;

    private int currentPage = 0;

    @MainThread
    public int getCurrentPage() {
        return currentPage;
    }

    private Flowable<List<Chat>> loadingChatsFlowable;

    @MainThread
    private int getCurrentOffset() {
        return getCurrentPage() * PAGE_SIZE;
    }

    @MainThread
    public boolean haveMoreChats() {
        return chatsSource.getCount() > getCurrentOffset();
    }

    @MainThread
    public Flowable<List<Chat>> loadMoreChats(){
        if(!haveMoreChats()){
            return Flowable.fromCallable(()-> chatsStorage.getChatList());
        }
        if (loadingChatsFlowable == null) {
            loadingChatsFlowable = chatsSource.execute(getCurrentOffset(), PAGE_SIZE)
                    .doOnNext(description -> {if (description.getLastTransaction().getHeight() > maxHeight) {maxHeight = description.getLastTransaction().getHeight();}})
                    .doOnNext(description -> keyStorage.savePublicKeysFromParticipant(description))
                    .flatMap(this::mapToChat)
                    .toList()
                    .toFlowable()
                    .map(list -> {
                        chatsStorage.updateLastMessages();
                        return chatsStorage.getChatList();
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(() -> {
                        loadingChatsFlowable = null;
                        currentPage++;
                    })
                    .retry(throwable -> throwable instanceof IOException)
                    .doOnError(e -> {
                        loadingChatsFlowable = null;
                        if(!(e instanceof IOException)){
                            currentPage++;
                        }
                    })
            .share();
        }
        return loadingChatsFlowable;
    }

    public Flowable<Chat> loadChats() {
        return chatsSource
                .execute()
                .doOnNext(description -> {if (description.getLastTransaction().getHeight() > maxHeight) {maxHeight = description.getLastTransaction().getHeight();}})
                .doOnNext(description -> keyStorage.savePublicKeysFromParticipant(description))
                .doOnComplete(() -> chatsStorage.setLoaded(true))
                .flatMap(this::mapToChat);
    }

    public Completable loadContacts() {
        return contactsSource
                .execute()
                .doOnNext(contacts -> chatsStorage.refreshContacts(contacts))
                .ignoreElements();
    }
    public Single<Long> update() {
       return newItemsSource
                .execute(maxHeight)
                .doOnNext(transaction -> {if (transaction.getHeight() > maxHeight) {maxHeight = transaction.getHeight();}})
                .flatMap(transaction -> Flowable
                        .fromCallable(() -> keyStorage.combinePublicKeyWithTransaction(transaction))
                        .map(transactionPair -> messageMapper.apply(transactionPair))
                        .doOnNext(message -> chatsStorage.addMessageToChat(message))
                        .onErrorReturn(throwable -> new FallbackMessage())
                )
                .count()
                .doAfterSuccess(count -> {
                    if (count > 0) {
                        chatsStorage.updateLastMessages();
                    }
                });

    }

    public Flowable<AbstractMessage> loadHistory(String chatId) {
        return historySource.execute(chatId)
                .doOnNext(transaction -> {if (transaction.getHeight() > maxHeight) {maxHeight = transaction.getHeight();}})
                .map(transaction -> keyStorage.combinePublicKeyWithTransaction(transaction))
                .flatMap(pair -> Flowable.just(pair)
                        .map(transaction -> messageMapper.apply(transaction))
                        .onErrorReturn(throwable -> {
                            FallbackMessage fallbackMessage = new FallbackMessage();
                            fallbackMessage.setError(throwable.getMessage());
                            fallbackMessage.setSupportedType(SupportedMessageListContentType.FALLBACK);
                            if (throwable instanceof MessageDecryptException) {
                                fallbackMessage.setCompanionId(((MessageDecryptException) throwable).getCompanionId());
                                fallbackMessage.setiSay(((MessageDecryptException) throwable).isISay());
                                fallbackMessage.setTimestamp(((MessageDecryptException) throwable).getTimestamp());
                                fallbackMessage.setStatus(AbstractMessage.Status.INVALIDATED);
                                fallbackMessage.setTransactionId(((MessageDecryptException) throwable).getTransactionId());
                            }
                            return fallbackMessage;
                        })
                )
                .doOnNext(message -> chatsStorage.addMessageToChat(message))
                .doOnComplete(() -> chatsStorage.updateLastMessages());
    }

    private Flowable<Chat> mapToChat(ChatList.ChatDescription description) {
        return Flowable.fromCallable(() -> keyStorage
                .combinePublicKeyWithTransaction(
                        description.getLastTransaction()
                ))
                .doOnError(throwable -> LoggerHelper.e(getClass().getSimpleName(), throwable.getMessage(), throwable))
                .onErrorReturnItem(new Pair<>(null, null))
                .map(transactionPair -> {
                    if ((transactionPair.first != null) && (transactionPair.second != null)) {
                        Chat chat = chatMapper.apply(transactionPair);
                        AbstractMessage message = messageMapper.apply(transactionPair);
                        chat.setLastMessage(message);
                        chatsStorage.addNewChat(chat);
                        return chat;
                    } else {
                        return new Chat();
                    }
                });
    }
}
