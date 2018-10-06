package im.adamant.android.ui.messages_support.holders;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import im.adamant.android.ui.messages_support.entities.MessageListContent;

public abstract class AbstractMessageListContentViewHolder extends RecyclerView.ViewHolder {
    protected Context context;

    public AbstractMessageListContentViewHolder(Context context, View v) {
        super(v);
        this.context = context;
    }

    public abstract void bind(MessageListContent message);
}
