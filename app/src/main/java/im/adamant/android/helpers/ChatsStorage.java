package im.adamant.android.helpers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.entities.Contact;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import im.adamant.android.ui.messages_support.entities.MessageListContent;
import im.adamant.android.ui.messages_support.entities.Separator;

public class ChatsStorage {
    //TODO: So far, the manipulation of the chat lists is entrusted to this interactor, but perhaps over time it's worth changing
    //TODO: Multithreaded access to properties can cause problems in the future
    private HashMap<String, List<MessageListContent>> messagesByChats = new HashMap<>();
    private List<Chat> chats = new ArrayList<>();
    private Map<String, List<Long>> separators = new HashMap<>();
    private Calendar separatorCalendar = Calendar.getInstance();
    private long contactsVersion = 0;

    public List<Chat> getChatList() {
        return chats;
    }

    public List<MessageListContent> getMessagesByCompanionId(String companionId) {
        List<MessageListContent> requestedMessages = messagesByChats.get(companionId);

        if (requestedMessages == null){return new ArrayList<>();}

        return requestedMessages;
    }

    public void addNewChat(Chat chat) {
        int index = chats.indexOf(chat);
        if (index == -1){
            chats.add(chat);
            messagesByChats.put(chat.getCompanionId(), new ArrayList<>());
        }
    }

    public void addMessageToChat(MessageListContent message) {
        List<MessageListContent> messages = messagesByChats.get(message.getCompanionId());

        if (messages == null) {
            messages = new ArrayList<>();
            messagesByChats.put(message.getCompanionId(), messages);
        }

        //If we sent this message and it's already in the list
        if (!messages.contains(message)){
            addSeparatorIfNeeded(messages, message);
            messages.add(message);
        }
    }

    public void updateLastMessages() {
        //Setting last message to chats
        for(Chat chat : chats){
            List<MessageListContent> messages = messagesByChats.get(chat.getCompanionId());
            if (messages != null && messages.size() > 0){
                for (int i = (messages.size() - 1); i >= 0; i--){
                    MessageListContent mes = messages.get(i);
                    boolean isMessageWithContent = (mes != null && mes.getSupportedType() != SupportedMessageListContentType.SEPARATOR);
                    if (isMessageWithContent){
                        AbstractMessage message = (AbstractMessage)mes;
                        chat.setLastMessage(message);
                        break;
                    }
                }
            }
        }
    }

    public void refreshContacts(Map<String, Contact> contacts, long currentVersion) {
        if (currentVersion > contactsVersion){
            for (Map.Entry<String, Contact> contactEntry : contacts.entrySet()){
                String companionId = contactEntry.getKey();
                Contact contact = contactEntry.getValue();

                Chat chat = new Chat();
                chat.setCompanionId(companionId);

                if (chats.contains(chat)) {
                    int index = chats.indexOf(chat);
                    Chat originalChat = chats.get(index);
                    originalChat.setTitle(contact.getDisplayName());
                }
            }
        }
    }

    public Chat findChatByCompanionId(String companionId) {
        Chat chat = new Chat();
        chat.setCompanionId(companionId);

        if (chats.contains(chat)){
            int index = chats.indexOf(chat);
            return chats.get(index);
        } else {
            return null;
        }
    }

    public Map<String, Contact> getContacts() {
        Map<String, Contact> contacts = new HashMap<>();

        for (Chat chat : chats) {
            if (!chat.getTitle().equalsIgnoreCase(chat.getCompanionId())){
                Contact contact = new Contact();
                contact.setDisplayName(chat.getTitle());
                contacts.put(chat.getCompanionId(), contact);
            }
        }

        return contacts;
    }

    public void cleanUp() {
        chats.clear();
        messagesByChats.clear();
        contactsVersion = 0;
    }


    private void addSeparatorIfNeeded(List<MessageListContent> messages, MessageListContent message) {
        //Получи дату сообщения и проверь что сепаратор для этого сообщения уже добавлен в специальный список
        //если его нет то создай
        separatorCalendar.setTimeInMillis(message.getTimestamp());
        separatorCalendar.set(Calendar.HOUR_OF_DAY, 0);
        separatorCalendar.set(Calendar.MINUTE, 0);
        separatorCalendar.set(Calendar.SECOND, 0);
        separatorCalendar.set(Calendar.MILLISECOND, 0);

        long startDayTimestamp = separatorCalendar.getTimeInMillis();
        List<Long> separatorsForChat = separators.get(message.getCompanionId());
        if (separatorsForChat == null){
            Separator separator = new Separator();
            separator.setCompanionId(message.getCompanionId());
            separator.setTimestamp(startDayTimestamp);
            messages.add(separator);

            separatorsForChat = new ArrayList<>();
            separatorsForChat.add(startDayTimestamp);
            separators.put(message.getCompanionId(), separatorsForChat);
        } else {
            if (!separatorsForChat.contains(startDayTimestamp)){
                Separator separator = new Separator();
                separator.setCompanionId(message.getCompanionId());
                separator.setTimestamp(startDayTimestamp);
                messages.add(separator);

                separatorsForChat.add(startDayTimestamp);
            }
        }

    }
}
