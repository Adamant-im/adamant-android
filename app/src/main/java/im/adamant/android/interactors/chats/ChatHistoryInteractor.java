package im.adamant.android.interactors.chats;

import androidx.annotation.MainThread;

import java.io.IOException;
import java.util.List;

import im.adamant.android.core.exceptions.MessageDecryptException;
import im.adamant.android.helpers.PublicKeyStorage;
import im.adamant.android.rx.AbstractObservableRxList;
import im.adamant.android.ui.mappers.TransactionToMessageMapper;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import im.adamant.android.ui.messages_support.entities.FallbackMessage;
import im.adamant.android.ui.messages_support.entities.MessageListContent;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ChatHistoryInteractor {

    public static final int PAGE_SIZE = 25;

    private String chatId;


    private ChatsStorage chatsStorage;
    private PublicKeyStorage
            keyStorage;

    private TransactionToMessageMapper messageMapper;

    private Flowable<AbstractObservableRxList<MessageListContent>> loadingMessagesFlowable;
    private HistoryTransactionsSource historySource;

    private int maxHeight = 1;
    private int currentPage = 0;

    public ChatHistoryInteractor(HistoryTransactionsSource historySource, ChatsStorage chatsStorage,
                                 PublicKeyStorage keyStorage,
                                 TransactionToMessageMapper messageMapper) {
        this.historySource = historySource;
        this.chatsStorage = chatsStorage;
        this.keyStorage = keyStorage;
        this.messageMapper = messageMapper;
    }

    public boolean haveMoreMessages() {
        return historySource.getCount() > getCurrentOffset();
    }

    @MainThread
    public int getCurrentPage() {
        return currentPage;
    }

    @MainThread
    private int getCurrentOffset() {
        return getCurrentPage() * PAGE_SIZE;
    }

    @MainThread
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    @MainThread
    public int getMaxHeight() {
        return maxHeight;
    }

    @MainThread
    private void updateHeight(int height) {
        maxHeight = Math.max(maxHeight, height);
    }

    @MainThread
    public Flowable<AbstractObservableRxList<MessageListContent>> loadMoreMessages() {
        if (chatId == null) {
            return Flowable.error(new IllegalStateException("Chat setChatId must be called"));
        }
        if (!haveMoreMessages()) {
            return Flowable.fromCallable(() -> chatsStorage.getMessagesByCompanionId(chatId));
        }
        if (loadingMessagesFlowable == null) {
            loadingMessagesFlowable = historySource.execute(chatId, getCurrentOffset(), PAGE_SIZE)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(transaction -> updateHeight(transaction.getHeight()))
                    .observeOn(Schedulers.computation())
                    .map(transaction -> keyStorage.combinePublicKeyWithTransaction(transaction))
                    .flatMap(pair -> Flowable.just(pair)
                            .map(transaction -> (MessageListContent) messageMapper.apply(transaction))
                            .onErrorReturn(FallbackMessage::createMessageFromThrowable)
                    )
                    .toList()
                    .toFlowable()
                    .doOnNext(messages -> {
                        for (MessageListContent messageListContent : messages) {
                            chatsStorage.addMessageToChat(messageListContent);
                        }
                    })
                    .doOnComplete(() -> chatsStorage.updateLastMessages())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(ignored -> {
                        loadingMessagesFlowable = null;
                        currentPage++;
                    })
                    .observeOn(Schedulers.computation())
                    .map(ignored -> chatsStorage.getMessagesByCompanionId(chatId))
                    .retry(throwable -> throwable instanceof IOException)
                    .doOnError(e -> {
                        loadingMessagesFlowable = null;
                        if (!(e instanceof IOException)) {
                            currentPage++;
                        }
                    })
                    .share();
        }
        return loadingMessagesFlowable;
    }
}
