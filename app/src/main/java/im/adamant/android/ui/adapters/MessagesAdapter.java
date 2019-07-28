package im.adamant.android.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import im.adamant.android.R;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.rx.AbstractObservableRxList;
import im.adamant.android.rx.ThreadUnsafeObservableRxList;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import im.adamant.android.ui.messages_support.entities.MessageListContent;
import im.adamant.android.ui.messages_support.factories.MessageFactory;
import im.adamant.android.ui.messages_support.factories.MessageFactoryProvider;
import im.adamant.android.ui.messages_support.holders.AbstractMessageListContentViewHolder;
import im.adamant.android.ui.messages_support.holders.SeparatorViewHolder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class MessagesAdapter extends RecyclerView.Adapter<AbstractMessageListContentViewHolder> {

    private AbstractObservableRxList<MessageListContent> messages = new ThreadUnsafeObservableRxList<>();
    private MessageFactoryProvider messageFactoryProvider;
    private Disposable messageListUpdateSubscription;

    public MessagesAdapter(MessageFactoryProvider messageFactoryProvider, AbstractObservableRxList<MessageListContent> messages) {
        this.messageFactoryProvider = messageFactoryProvider;

        if (messages != null){
            this.messages = messages;
            subscribe(messages);
        }
    }

    @Override
    public int getItemViewType(int position) {
        MessageListContent message = messages.get(position);
        return message.getSupportedType().ordinal();
    }

    @NonNull
    @Override
    public AbstractMessageListContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            int separatorType = SupportedMessageListContentType.SEPARATOR.ordinal();
            if (viewType == separatorType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View v = inflater.inflate(R.layout.list_item_separator_message, parent, false);
                return new SeparatorViewHolder(parent.getContext(), v);
            } else {
                MessageFactory messageFactory = messageFactoryProvider.getFactoryByType(
                        SupportedMessageListContentType.values()[viewType]
                );

                return messageFactory.getViewHolder(parent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public void onBindViewHolder(@NonNull AbstractMessageListContentViewHolder holder, int position) {
        MessageListContent message = messages.get(position);
        boolean isLastMessage = !(position < (messages.size() - 1));
        holder.bind(message, detectNextMessageWithSameSender(position), isLastMessage);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void updateDataset(AbstractObservableRxList<MessageListContent> messages){
        //If not same object, its right check
        if (messages != null && this.messages != messages) {
            this.messages = messages;
            notifyDataSetChanged();
            subscribe(messages);
        }
    }

    private void subscribe(AbstractObservableRxList<MessageListContent> messages) {
        if (this.messageListUpdateSubscription != null) { this.messageListUpdateSubscription.dispose();}
        messageListUpdateSubscription = messages
                .getEventObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        event -> notifyItemChanged(event.getPosition(), event.getCount()),
                        error -> LoggerHelper.e(getClass().getSimpleName(), error.getMessage(), error)
                );
    }

    private boolean detectNextMessageWithSameSender(int position) {
        if (position == 0) { return false; }

        MessageListContent currentMessage = messages.get(position);
        if (currentMessage.getSupportedType() == SupportedMessageListContentType.SEPARATOR) { return false; }

        MessageListContent nextMessage = null;
        int i = 1;
        do {
            if ((position - i) <= 0) { return false; }

            nextMessage = messages.get(position - i);
            if (nextMessage == null) { return false; }

            i++;
        } while (nextMessage.getSupportedType() == SupportedMessageListContentType.SEPARATOR);

        return ((AbstractMessage)currentMessage).isiSay() == ((AbstractMessage)nextMessage).isiSay();
    }

    @Override
    protected void finalize() throws Throwable {
        if (this.messageListUpdateSubscription != null) { this.messageListUpdateSubscription.dispose();}
        super.finalize();
    }
}
