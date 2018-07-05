package im.adamant.android.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import im.adamant.android.R;
import im.adamant.android.ui.entities.Chat;
import com.github.curioustechizen.ago.RelativeTimeTextView;

import java.util.ArrayList;
import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder>{
    private List<Chat> chats = new ArrayList<>();
    private SelectItemListener listener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView chatView;
        public TextView lastMessageView;
        public RelativeTimeTextView dateView;
        public ViewHolder(View v) {
            super(v);
            chatView = v.findViewById(R.id.list_item_chat_name);
            lastMessageView = v.findViewById(R.id.list_item_chat_last_message);
            dateView = v.findViewById(R.id.list_item_chat_last_message_date);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(listener != null) {
                listener.itemWasSelected(chats.get(getAdapterPosition()));
            }
        }

        public void bind(Chat chat) {
            chatView.setText(chat.getTitle());
            if (chat.getLastMessage() != null){
                lastMessageView.setText(chat.getLastMessage().getShortedMessage(50));
                dateView.setReferenceTime(chat.getLastMessage().getDate());
            } else {
                lastMessageView.setText("");
                dateView.setReferenceTime(System.currentTimeMillis());
            }
        }
    }

    public interface SelectItemListener {
        void itemWasSelected(Chat chat);
    }

    public ChatsAdapter(List<Chat> chats, SelectItemListener listener) {
        if (chats != null){
            this.chats = chats;
        }

        if (listener != null){
            this.listener = listener;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_chat, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Chat chat = chats.get(position);
        if (chat != null){
            holder.bind(chat);
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public void updateDataset(List<Chat> chats){
        if (chats != null){
            this.chats = chats;
        } else {
            this.chats.clear();
        }

        notifyDataSetChanged();
    }


    public void setListener(SelectItemListener listener) {
        this.listener = listener;
    }
}
