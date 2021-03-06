package im.adamant.android.interactors.chats;

import android.annotation.SuppressLint;
import android.util.Pair;

import androidx.annotation.MainThread;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Provider;

import im.adamant.android.core.responses.ChatList;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.helpers.PublicKeyStorage;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.mappers.TransactionToChatMapper;
import im.adamant.android.ui.mappers.TransactionToMessageMapper;
import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import im.adamant.android.ui.messages_support.entities.FallbackMessage;
import im.adamant.android.ui.messages_support.entities.MessageListContent;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static im.adamant.android.ui.messages_support.SupportedMessageListContentType.ADAMANT_TRANSFER_MESSAGE;

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
    private Provider<ChatHistoryInteractor> chatHistoryInteractorProvider;

    private int maxHeight = 1;

    public ChatInteractor(
            PublicKeyStorage keyStorage,
            NewTransactionsSource newItemsSource,
            LastTransactionInChatsSource chatsSource,
            HistoryTransactionsSource historySource,
            ContactsSource contactsSource,
            ChatsStorage chatsStorage,
            TransactionToChatMapper chatMapper,
            TransactionToMessageMapper messageMapper,Provider<ChatHistoryInteractor> chatInteractorProvider
    ) {
        this.keyStorage = keyStorage;
        this.newItemsSource = newItemsSource;
        this.chatsSource = chatsSource;
        this.historySource = historySource;
        this.contactsSource = contactsSource;
        this.chatsStorage = chatsStorage;
        this.chatMapper = chatMapper;
        this.messageMapper = messageMapper;
        this.chatHistoryInteractorProvider = chatInteractorProvider;
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

    private boolean contactsLoaded;

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
                    .doOnError(e -> {
                        loadingChatsFlowable = null;
                        if(!(e instanceof IOException)){
                            currentPage++;
                        }
                    });

            if (!contactsLoaded) {
                loadingChatsFlowable = loadContacts().andThen(loadingChatsFlowable)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(()->{
                    contactsLoaded  = true;
                });
            }
            loadingChatsFlowable = loadingChatsFlowable.share();
        }
        return loadingChatsFlowable;
    }

    public Completable loadContacts() {
        return contactsSource
                .execute()
                .doOnNext(contacts -> chatsStorage.saveContacts(contacts))
                .ignoreElements();
    }

    @SuppressWarnings("RedundantIfStatement")
    private boolean haveText(AbstractMessage message) {
        if (message.getSupportedType() == ADAMANT_TRANSFER_MESSAGE) {
            return false;
        }
        //TODO return false on type 8 with empty text
        return true;
    }

    public Single<Long> update() {
       return newItemsSource
               .execute(maxHeight)
               .doOnNext(transaction -> {if (transaction.getHeight() > maxHeight) {maxHeight = transaction.getHeight();}})
               .doOnNext(transaction -> {
                   if (transaction.getRecipientPublicKey() != null && !transaction.getRecipientPublicKey().isEmpty()) {
                       keyStorage.setPublicKey(transaction.getRecipientId(), transaction.getRecipientPublicKey());
                   }

                   if (transaction.getSenderPublicKey() != null && !transaction.getSenderPublicKey().isEmpty()) {
                       keyStorage.setPublicKey(transaction.getSenderId(), transaction.getSenderPublicKey());
                   }
               })
               .flatMap(transaction -> Flowable
                        .fromCallable(() -> keyStorage.combinePublicKeyWithTransaction(transaction))
                        .map(transactionPair -> messageMapper.apply(transactionPair))
                        .onErrorReturn(throwable -> {
                            LoggerHelper.e(getClass().getSimpleName(), throwable.getMessage(), throwable);
                            return FallbackMessage.createMessageFromThrowable(throwable);
                        })
                        .groupBy(AbstractMessage::getCompanionId)
                        .map(chatMessagesFlowable -> chatMessagesFlowable.filter(this::haveText))
                        .map(chatMessagesFlowable -> chatMessagesFlowable.doOnNext(message -> {
                            Chat chat = new Chat();
                            chat.setTitle(message.getCompanionId());
                            chat.setCompanionId(message.getCompanionId());
                            chatsStorage.addNewChat(chat);
                        }))
                        .flatMap(chatMessagesFlowable -> chatMessagesFlowable)
                        .doOnNext(message -> chatsStorage.addMessageToChat(message))
               )
               .count()
               .doAfterSuccess(count -> {
                    if (count > 0) {
                        chatsStorage.updateLastMessages();
                    }
               });

    }

    private Map<String,ChatHistoryInteractor> chatsInteractors = new TreeMap<>();

    @MainThread
    private ChatHistoryInteractor getInteractorForChat(String chatId){
        if(!chatsInteractors.containsKey(chatId)) {
            ChatHistoryInteractor interactor = chatHistoryInteractorProvider.get();
            interactor.setChatId(chatId);
            chatsInteractors.put(chatId, interactor);
        }
        return chatsInteractors.get(chatId);
    }


    @MainThread
    public boolean haveMoreChatMessages(String chatId){
        ChatHistoryInteractor chatHistoryInteractor = getInteractorForChat(chatId);
        return chatHistoryInteractor.haveMoreMessages();
    }

    @MainThread
    public Flowable<List<MessageListContent>> loadMoreChatMessages(String chatId) {
        ChatHistoryInteractor chatHistoryInteractor = getInteractorForChat(chatId);
        return chatHistoryInteractor.loadMoreMessages()
                .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete(()-> maxHeight = Math.max(maxHeight, chatHistoryInteractor.getMaxHeight()))
            .share();
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("CheckResult")
    @MainThread
    public void startInitialLoading(){
        loadMoreChats()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(chats -> {
                    for(Chat chat:chats.subList(0, Math.min(5, chats.size()))) {
                        loadMoreChatMessages(chat.getCompanionId())
                                .subscribe();
                    }
                });
    }

    @MainThread
    public void resetPagingState(){
        loadingChatsFlowable = null;
        chatsInteractors.clear();;
        contactsLoaded = false;
        currentPage = 0;
        chatsSource.resetState();
    }
}
