package im.adamant.android.rx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.entities.Contact;
import im.adamant.android.ui.entities.messages.AbstractMessage;
import io.reactivex.Flowable;
import io.reactivex.subjects.PublishSubject;

public class ChatsStorage {
    //TODO: So far, the manipulation of the chat lists is entrusted to this interactor, but perhaps over time it's worth changing
    //TODO: Multithreaded access to properties can cause problems in the future
    private HashMap<String, List<AbstractMessage>> messagesByChats = new HashMap<>();
    private List<Chat> chats = new ArrayList<>();
    protected final PublishSubject<ChatEvent<Chat>> subject = PublishSubject.create();
    private long contactsVersion = 0;

    public List<Chat> getChatList() {
        return chats;
    }

    public List<AbstractMessage> getMessagesByCompanionId(String companionId) {
        List<AbstractMessage> requestedMessages = messagesByChats.get(companionId);

        if (requestedMessages == null){return new ArrayList<>();}

        return requestedMessages;
    }

    public void addNewChat(Chat chat) {
        if (chats.indexOf(chat) == -1){
            chats.add(chat);
            messagesByChats.put(chat.getCompanionId(), new ArrayList<>());
        }
    }

    public void addMessageToChat(AbstractMessage message) {
        List<AbstractMessage> messages = messagesByChats.get(message.getCompanionId());

        if (messages != null) {
            //If we sent this message and it's already in the list
            if (!messages.contains(message)){
                messages.add(message);
            }
        } else {
            List<AbstractMessage> newMessageBlock = new ArrayList<>();
            newMessageBlock.add(message);
            messagesByChats.put(message.getCompanionId(), newMessageBlock);
        }
    }

    public void updateLastMessages() {
        //Setting last message to chats
        for(Chat chat : chats){
            List<AbstractMessage> messages = messagesByChats.get(chat.getCompanionId());
            if (messages != null && messages.size() > 0){
                AbstractMessage mes = messages.get(messages.size() - 1);
                if (mes != null){chat.setLastMessage(mes);}
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

    public PublishSubject<ChatEvent<Chat>> getObservable() {
        return subject;
    }

    public enum ChangeType {
        CHANGE_CONTACT_INFO
    }

    public static class ChatEvent<T> {
        public ChangeType changeType;
        public T item;

        public ChatEvent(ChangeType changeType, T item) {
            this.changeType = changeType;
            this.item = item;
        }
    }
}
