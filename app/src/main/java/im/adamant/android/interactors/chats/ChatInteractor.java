package im.adamant.android.interactors.chats;

import im.adamant.android.helpers.PublicKeyStorage;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.mappers.TransactionToChatMapper;
import im.adamant.android.ui.mappers.TransactionToMessageMapper;
import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import io.reactivex.Completable;
import io.reactivex.Single;

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

    public Completable loadChats() {
        return chatsSource
                .execute()
                .doOnNext(description -> {if (description.getLastTransaction().getHeight() > maxHeight) {maxHeight = description.getLastTransaction().getHeight();}})
                .doOnNext(description -> keyStorage.savePublicKeysFromParticipant(description))
                .map(description -> keyStorage.combinePublicKeyWithTransaction(description.getLastTransaction()))
                .map(transaction -> {
                    Chat chat = chatMapper.apply(transaction);
                    AbstractMessage message = messageMapper.apply(transaction);
                    chat.setLastMessage(message);

                    return chat;
                })
                .doOnNext(chat -> chatsStorage.addNewChat(chat))
                .ignoreElements()
                .andThen(contactsSource
                        .execute()
                        .doOnNext(contacts -> chatsStorage.refreshContacts(contacts))
                        .ignoreElements()
                );
    }

    public Single<Long> update() {
       return newItemsSource
                .execute(maxHeight)
                .doOnNext(transaction -> {if (transaction.getHeight() > maxHeight) {maxHeight = transaction.getHeight();}})
                .map(transaction -> keyStorage.combinePublicKeyWithTransaction(transaction))
                .map(transaction -> messageMapper.apply(transaction))
                .doOnNext(message -> chatsStorage.addMessageToChat(message))
                .count()
                .doAfterSuccess(count -> {
                    if (count > 0) {
                        chatsStorage.updateLastMessages();
                    }
                });

    }

    public Completable loadHistory(String chatId) {
        return historySource.execute(chatId)
                .doOnNext(transaction -> {if (transaction.getHeight() > maxHeight) {maxHeight = transaction.getHeight();}})
                .map(transaction -> keyStorage.combinePublicKeyWithTransaction(transaction))
                .map(transaction -> messageMapper.apply(transaction))
                .doOnNext(message -> chatsStorage.addMessageToChat(message))
                .doOnComplete(() -> chatsStorage.updateLastMessages())
                .ignoreElements();
    }
}