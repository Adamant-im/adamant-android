package im.adamant.android.interactors.chats;

import java.util.concurrent.TimeUnit;

import im.adamant.android.core.AdamantApi;
import im.adamant.android.rx.Irrelevant;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.mappers.ChatTransactionToChatMapper;
import im.adamant.android.ui.mappers.TransactionToMessageMapper;
import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import io.reactivex.Completable;
import io.reactivex.Flowable;

public class ChatInteractor {
    //TODO: Schedulers must be injected through Dagger for comfort unit-testing

    //TODO: The current height should be "Atomic" changed

    //TODO: Use database for save received transactions

    //TODO: Well test the erroneous execution path, replace where you need doOnError

    private NewTransactionsSource newItemsSource;
    private LastTransactionInChatsSource chatsSource;
    private HistoryTransactionsSource historySource;
    private ChatsStorage chatsStorage;
    private ChatTransactionToChatMapper chatMapper;
    private TransactionToMessageMapper messageMapper;

    private int maxHeight = 1;

    public ChatInteractor(
            NewTransactionsSource newItemsSource,
            LastTransactionInChatsSource chatsSource,
            HistoryTransactionsSource historySource,
            ChatsStorage chatsStorage,
            ChatTransactionToChatMapper chatMapper,
            TransactionToMessageMapper messageMapper
    ) {
        this.newItemsSource = newItemsSource;
        this.chatsSource = chatsSource;
        this.historySource = historySource;
        this.chatsStorage = chatsStorage;
        this.chatMapper = chatMapper;
        this.messageMapper = messageMapper;
    }

    public Completable loadChats() {
        //TODO: Load contact list here
        return chatsSource
                .execute()
                .doOnNext(transaction -> {if (transaction.getHeight() > maxHeight) {maxHeight = transaction.getHeight();}})
                .map(transaction -> {
                    Chat chat = chatMapper.apply(transaction);
                    AbstractMessage message = messageMapper.apply(transaction);
                    chat.setLastMessage(message);

                    return chat;
                })
                .doOnNext(chat -> chatsStorage.addNewChat(chat))
        .ignoreElements();
    }

    public Flowable<Irrelevant> update() {
       return newItemsSource
                .execute(maxHeight)
                .doOnNext(transaction -> {if (transaction.getHeight() > maxHeight) {maxHeight = transaction.getHeight();}})
                .map(transaction -> messageMapper.apply(transaction))
                .doOnNext(message -> chatsStorage.addMessageToChat(message))
                .flatMap(message -> Flowable.just(Irrelevant.INSTANCE))
                .repeatWhen((completed) -> completed.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS, TimeUnit.SECONDS));
    }

    public Completable loadHistory(String chatId) {
        return historySource.execute(chatId)
                .doOnNext(transaction -> {if (transaction.getHeight() > maxHeight) {maxHeight = transaction.getHeight();}})
                .map(transaction -> messageMapper.apply(transaction))
                .doOnNext(message -> chatsStorage.addMessageToChat(message))
                .ignoreElements();
    }
}
