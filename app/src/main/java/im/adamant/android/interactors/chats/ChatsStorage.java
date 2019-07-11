package im.adamant.android.interactors.chats;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.entities.Contact;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import im.adamant.android.ui.messages_support.entities.MessageListContent;
import im.adamant.android.ui.messages_support.entities.Separator;

public class ChatsStorage {
    private static final ReadWriteLock chatsLock = new ReentrantReadWriteLock();
    private static final Lock chatsReadLock = chatsLock.readLock();
    private static final Lock chatsWriteLock = chatsLock.writeLock();

    private final ChatsByLastMessageComparator chatComparator = new ChatsByLastMessageComparator();
    private final MessageComparator messageComparator = new MessageComparator();

    private HashMap<String, List<MessageListContent>> messagesByChats = new HashMap<>();
    private List<Chat> chats = new ArrayList<>();
    private Map<String, List<Long>> separators = new HashMap<>();

    private boolean isLoaded = false;

    public List<Chat> getChatList() {
        chatsReadLock.lock();
        try {
            return new ArrayList<>(chats);
        } finally {
            chatsReadLock.unlock();
        }
    }

    public List<MessageListContent> getMessagesByCompanionId(String companionId) {
        chatsReadLock.lock();
        try {
            List<MessageListContent> requestedMessages = messagesByChats.get(companionId);
            if (requestedMessages == null) {
                return new ArrayList<>();
            }
            return new ArrayList<>(requestedMessages);
        } finally {
            chatsReadLock.unlock();
        }
    }

    public void addNewChat(Chat chat) {
        chatsWriteLock.lock();
        try {
            int index = chats.indexOf(chat);
            if (index == -1) {
                chats.add(chat);
                messagesByChats.put(chat.getCompanionId(), new ArrayList<>());
            }
        } finally {
            chatsWriteLock.unlock();
        }
    }

    public void addMessageToChat(MessageListContent message) {
        chatsWriteLock.lock();
        try {
            List<MessageListContent> messages = messagesByChats.get(message.getCompanionId());

            if (messages == null) {
                messages = new ArrayList<>();
                messagesByChats.put(message.getCompanionId(), messages);
            }

            //If we sent this message and it's already in the list
            if (!messages.contains(message)) {
                addSeparatorIfNeeded(messages, message);
                messages.add(message);

                boolean isMessageWithContent = message.getSupportedType()
                        != SupportedMessageListContentType.SEPARATOR;
                if (isMessageWithContent) {
                    Chat updatedChat = findChatByCompanionId(message.getCompanionId());
                    if (updatedChat != null) {
                        MessageListContent lst = updatedChat.getLastMessage();
                        if (lst == null || lst.getTimestamp() < message.getTimestamp()) {
                            updatedChat.setLastMessage((AbstractMessage) message);
                        }
                    }
                }
            }
        } finally {
            chatsWriteLock.unlock();
        }

    }

    public void updateLastMessages() {
        //Setting last message to chats
        for (Chat chat : chats) {
            List<MessageListContent> messages = messagesByChats.get(chat.getCompanionId());
            if (messages != null && messages.size() > 0) {
                for (int i = (messages.size() - 1); i >= 0; i--) {
                    MessageListContent mes = messages.get(i);
                    boolean isMessageWithContent = (mes != null && mes.getSupportedType() != SupportedMessageListContentType.SEPARATOR);
                    if (isMessageWithContent) {
                        AbstractMessage message = (AbstractMessage) mes;
                        chat.setLastMessage(message);
                        break;
                    }
                }
            }
        }

        Collections.sort(chats, chatComparator);

        for (Map.Entry<String, List<MessageListContent>> entry : messagesByChats.entrySet()) {
            Collections.sort(entry.getValue(), messageComparator);
        }
    }

    public void refreshContacts(Map<String, Contact> contacts) {
        chatsWriteLock.lock();
        try {
            for (Map.Entry<String, Contact> contactEntry : contacts.entrySet()) {
                String companionId = contactEntry.getKey();
                Contact contact = contactEntry.getValue();

                if (contact.getDisplayName() == null || contact.getDisplayName().isEmpty()) {
                    continue;
                }

                Chat originalChat = findChatByCompanionId(companionId);
                if (originalChat != null) {
                    originalChat.setTitle(contact.getDisplayName());
                }
            }
        } finally {
            chatsWriteLock.unlock();
        }

    }

    public Chat findChatByCompanionId(String companionId) {
        chatsReadLock.lock();
        try {
            Chat chat = new Chat();
            chat.setCompanionId(companionId);

            if (chats.contains(chat)) {
                int index = chats.indexOf(chat);
                return chats.get(index);
            } else {
                return null;
            }
        } finally {
            chatsReadLock.unlock();
        }
    }

    public Map<String, Contact> getContacts() {
        chatsReadLock.lock();
        try {
            Map<String, Contact> contacts = new HashMap<>();

            for (Chat chat : chats) {
                if (!chat.getTitle().equalsIgnoreCase(chat.getCompanionId())) {
                    Contact contact = new Contact();
                    contact.setDisplayName(chat.getTitle());
                    contacts.put(chat.getCompanionId(), contact);
                }
            }

            return contacts;
        } finally {
            chatsReadLock.unlock();
        }
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    public void cleanUp() {
        chatsWriteLock.lock();
        try {
            chats.clear();
            messagesByChats.clear();
            separators.clear();
            isLoaded = false;
        } finally {
            chatsWriteLock.lock();
        }
    }

    private void addSeparatorIfNeeded(List<MessageListContent> messages, MessageListContent message) {
        //Получи дату сообщения и проверь что сепаратор для этого сообщения уже добавлен в специальный список
        //если его нет то создай
        Calendar separatorCalendar = Calendar.getInstance();
        separatorCalendar.setTimeInMillis(message.getTimestamp());
        separatorCalendar.set(Calendar.HOUR_OF_DAY, 0);
        separatorCalendar.set(Calendar.MINUTE, 0);
        separatorCalendar.set(Calendar.SECOND, 0);
        separatorCalendar.set(Calendar.MILLISECOND, 0);

        long startDayTimestamp = separatorCalendar.getTimeInMillis();
        List<Long> separatorsForChat = separators.get(message.getCompanionId());
        if (separatorsForChat == null) {
            Separator separator = new Separator();
            separator.setCompanionId(message.getCompanionId());
            separator.setTimestamp(startDayTimestamp);
            messages.add(separator);

            separatorsForChat = new ArrayList<>();
            separatorsForChat.add(startDayTimestamp);
            separators.put(message.getCompanionId(), separatorsForChat);
        } else {
            if (!separatorsForChat.contains(startDayTimestamp)) {
                Separator separator = new Separator();
                separator.setCompanionId(message.getCompanionId());
                separator.setTimestamp(startDayTimestamp);
                messages.add(separator);

                separatorsForChat.add(startDayTimestamp);
            }
        }
    }

    private static class ChatsByLastMessageComparator implements Comparator<Chat> {

        @Override
        public int compare(Chat o1, Chat o2) {
            if (o1 == o2) {
                return 0;
            }

            AbstractMessage firstObjectMessage = o1.getLastMessage();
            AbstractMessage secondObjectMessage = o2.getLastMessage();

            if (secondObjectMessage == null) {
                return 1;
            }
            if (firstObjectMessage == null) {
                return -1;
            }

            long diff = secondObjectMessage.getTimestamp() - firstObjectMessage.getTimestamp();

            if (diff > 0) {
                return 1;
            }
            if (diff == 0) {
                return 0;
            } else {
                return -1;
            }
        }
    }

    private static class MessageComparator implements Comparator<MessageListContent> {

        @Override
        public int compare(MessageListContent o1, MessageListContent o2) {
            if (o1 == o2) {
                return 0;
            }

            if (o2 == null) {
                return -1;
            }
            if (o1 == null) {
                return 1;
            }

            long diff = o2.getTimestamp() - o1.getTimestamp();

            if (diff > 0) {
                return -1;
            }
            if (diff == 0) {
                return 0;
            } else {
                return 1;
            }
        }
    }
}
