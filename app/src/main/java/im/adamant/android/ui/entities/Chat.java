package im.adamant.android.ui.entities;


import java.io.Serializable;
import java.util.Objects;

public class Chat implements Serializable {
    private String companionId;
    private Message lastMessage;
    private String title;

    public String getCompanionId() {
        return companionId;
    }

    public void setCompanionId(String companionId) {
        this.companionId = companionId;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        return Objects.equals(companionId, chat.companionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companionId);
    }
}
