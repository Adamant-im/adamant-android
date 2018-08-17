package im.adamant.android.ui.messages_support.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import im.adamant.android.ui.messages_support.entities.AbstractMessage;

public abstract class AbstractMessageViewHolder extends RecyclerView.ViewHolder {
    protected Context context;

    public AbstractMessageViewHolder(Context context, View v) {
        super(v);
        this.context = context;
    }

    public abstract void bind(AbstractMessage message);
}
