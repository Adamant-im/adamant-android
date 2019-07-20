package im.adamant.android.core.responses;

import java.util.List;

import im.adamant.android.core.entities.Participant;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.transaction_assets.TransactionAsset;

public class ChatList {

    public static class ChatDescription {
        private Transaction<? super TransactionAsset> lastTransaction;
        private List<Participant> participants;

        public Transaction<? super TransactionAsset> getLastTransaction() {
            return lastTransaction;
        }

        public void setLastTransaction(Transaction<? super TransactionAsset> lastTransaction) {
            this.lastTransaction = lastTransaction;
        }

        public List<Participant> getParticipants() {
            return participants;
        }

        public void setParticipants(List<Participant> participants) {
            this.participants = participants;
        }
    }

    private boolean success;
    private int nodeTimestamp;
    private int count;
    private List<ChatDescription> chats;
    private String error;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getNodeTimestamp() {
        return nodeTimestamp;
    }

    public void setNodeTimestamp(int nodeTimestamp) {
        this.nodeTimestamp = nodeTimestamp;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<ChatDescription> getChats() {
        return chats;
    }

    public void setChats(List<ChatDescription> chats) {
        this.chats = chats;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
