package com.dremanovich.adamant_android.ui.entities;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Chat implements Serializable{
    private String interlocutorId;
    private List<Message> messages = new ArrayList<>();

    public String getInterlocutorId() {
        return interlocutorId;
    }

    public void setInterlocutorId(String interlocutorId) {
        this.interlocutorId = interlocutorId;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
