package im.adamant.android.cases.interactors.chats;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import im.adamant.android.interactors.chats.ChatsStorage;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.messages_support.entities.AdamantBasicMessage;
import im.adamant.android.ui.messages_support.entities.MessageListContent;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ChatStorageTest {
    public static final int CHAT_COUNT = 10_000;
    public static final int TIMEOUT_SECONDS = 30;
    public static final int CONSUME_INTERVAL_NANOSECONDS = 100;

    @Test
    public void concurrentOneProducerOneConsumer() {
        ChatsStorage chatsStorage = new ChatsStorage();

        Flowable<AdamantBasicMessage> producer = provideProducer(chatsStorage, 1, CHAT_COUNT);
        Flowable<Integer> consumer = provideConsumer(chatsStorage, CHAT_COUNT);

        Disposable producerSubscription = producer.subscribe();
        Integer size = consumer.blockingFirst();

        Assert.assertEquals(CHAT_COUNT, (int) size);
    }

    @Test
    public void concurrentTwoProducerOneConsumer() {
        ChatsStorage chatsStorage = new ChatsStorage();

        Flowable<AdamantBasicMessage> producer1 = provideProducer(chatsStorage, 1, CHAT_COUNT);
        Flowable<AdamantBasicMessage> producer2 = provideProducer(chatsStorage, CHAT_COUNT + 1, CHAT_COUNT);
        Flowable<Integer> consumer = provideConsumer(chatsStorage, CHAT_COUNT * 2);

        Disposable producerSubscription1 = producer1.subscribe();
        Disposable producerSubscription2 = producer2.subscribe();
        Integer size = consumer.blockingFirst();

        Assert.assertEquals(CHAT_COUNT * 2, (int) size);
    }

    @Test
    public void testMessagesAndChatOrder() {
        final int count = 3;
        ChatsStorage chatsStorage = new ChatsStorage();

        Flowable<AdamantBasicMessage> adamantBasicMessageFlowable =  provideProducer(chatsStorage, 1, CHAT_COUNT)
                .zipWith(Flowable.range(1, count), (message, index) -> {
                    AdamantBasicMessage secondMessage = new AdamantBasicMessage();
                    secondMessage.setCompanionId(message.getCompanionId());
                    secondMessage.setText("Second Message " + index);
                    secondMessage.setTimestamp(Integer.MAX_VALUE - (index + 1));

                    chatsStorage.addMessageToChat(secondMessage);

                    message.setTimestamp(Integer.MAX_VALUE - index); //Добавлено раньше но должно выводится последним
                    return message;
                });

        adamantBasicMessageFlowable.blockingSubscribe();

        chatsStorage.updateLastMessages();

        List<Chat> chatList = chatsStorage.getChatList();
        Chat chat = chatList.get(chatList.size() - 1);

        Assert.assertEquals("U" + count, chat.getCompanionId());

        List<MessageListContent> messages = chatsStorage.getMessagesByCompanionId(chat.getCompanionId());

        Assert.assertEquals(2, messages.size());
        MessageListContent messageListContent = messages.get(messages.size() - 1);

        long timestamp = messageListContent.getTimestamp();
        String text = ((AdamantBasicMessage) messageListContent).getText();

        Assert.assertEquals(Integer.MAX_VALUE - 1, timestamp);
        Assert.assertEquals("Text: " + count, text);
    }


    private Flowable<AdamantBasicMessage> provideProducer(ChatsStorage chatsStorage, int start, int count) {
        return Flowable
                .range(start, CHAT_COUNT)
                .subscribeOn(Schedulers.io())
                .map(integer -> {
                    AdamantBasicMessage message = new AdamantBasicMessage();
                    message.setTransactionId(Integer.toString(integer));
                    message.setCompanionId("U" + integer);
                    message.setText("Text: " + integer);

                    return message;
                })
                .doOnNext(adamantBasicMessage -> {
                    Chat chat = new Chat();
                    chat.setCompanionId(adamantBasicMessage.getCompanionId());

                    chatsStorage.addNewChat(chat);
                    chatsStorage.addMessageToChat(adamantBasicMessage);
                })
                .doOnError(Throwable::printStackTrace);
    }

    private Flowable<Integer> provideConsumer(ChatsStorage chatsStorage, int expectedCount) {
        return Flowable
                .interval(CONSUME_INTERVAL_NANOSECONDS, TimeUnit.NANOSECONDS)
                .map(i -> {
                    for (Chat chat : chatsStorage.getChatList()) {
                        Assert.assertNotNull(chat);

                        for (MessageListContent message: chatsStorage.getMessagesByCompanionId(chat.getCompanionId())) {
                            Assert.assertNotNull(message);
                        }
                    }
                    return chatsStorage.getChatList().size();
                })
                .onBackpressureLatest()
                .filter(i -> i == expectedCount)
                .timeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .observeOn(Schedulers.newThread());
    }
}
