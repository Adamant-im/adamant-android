package im.adamant.android.interactors.chats;

import androidx.annotation.MainThread;

import java.io.IOException;
import java.util.List;

import im.adamant.android.core.exceptions.MessageDecryptException;
import im.adamant.android.helpers.PublicKeyStorage;
import im.adamant.android.ui.mappers.TransactionToMessageMapper;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import im.adamant.android.ui.messages_support.entities.FallbackMessage;
import im.adamant.android.ui.messages_support.entities.MessageListContent;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ChatHistoryInteractor {

    public static final int PAGE_SIZE = 3;

    private String chatId;

    private int currentPage = 0;

    @MainThread
    public int getCurrentPage() {
        return currentPage;
    }

    @MainThread
    private int getCurrentOffset() {
        return getCurrentPage() * PAGE_SIZE;
    }


    private HistoryTransactionsSource historySource;
    private ChatsStorage chatsStorage;
    private PublicKeyStorage
            keyStorage;

    private TransactionToMessageMapper messageMapper;

    public ChatHistoryInteractor(HistoryTransactionsSource historySource, ChatsStorage chatsStorage,
                                 PublicKeyStorage keyStorage,
                                 TransactionToMessageMapper messageMapper, String chatId) {
        this.historySource = historySource;
        this.chatId = chatId;
        this.chatsStorage = chatsStorage;
        this.keyStorage = keyStorage;
        this.messageMapper = messageMapper;
    }


    private Flowable<List<MessageListContent>> loadingMessagesFlowable;

    private boolean haveMoreMessages() {
        return historySource.getCount() > getCurrentOffset();
    }

    private int maxHeight = 1;

    @MainThread
    public int getMaxHeight() {
        return maxHeight;
    }

    @MainThread
    private void updateHeight(int height) {
        maxHeight = Math.max(maxHeight, height);
    }

    @MainThread
    public Flowable<List<MessageListContent>> loadMoreMessages() {
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
                    .toList()
                    .toFlowable()
                    .doOnNext(messages -> {
                        for (MessageListContent messageListContent : messages) {
                            chatsStorage.addMessageToChat(messageListContent);
                        }
                    })
                    .doOnComplete(() -> chatsStorage.updateLastMessages())
                    .map(ignored -> chatsStorage.getMessagesByCompanionId(chatId))
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(() -> {
                        loadingMessagesFlowable = null;
                        currentPage++;
                    })
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
