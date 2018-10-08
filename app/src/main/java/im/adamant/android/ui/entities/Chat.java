package im.adamant.android.ui.entities;


import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Objects;

import im.adamant.android.ui.messages_support.entities.AbstractMessage;

public class Chat implements Serializable {
    private String companionId;
    private AbstractMessage lastMessage;
    private String title;
    private String companionPublicKey;

    public String getCompanionId() {
        return companionId;
    }

    public void setCompanionId(String companionId) {
        this.companionId = companionId;
    }

    public AbstractMessage getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(AbstractMessage lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompanionPublicKey() {
        return companionPublicKey;
    }

    public void setCompanionPublicKey(String companionPublicKey) {
        this.companionPublicKey = companionPublicKey;
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
