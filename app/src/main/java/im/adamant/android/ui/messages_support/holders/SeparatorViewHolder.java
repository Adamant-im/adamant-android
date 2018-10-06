package im.adamant.android.ui.messages_support.holders;

import android.content.Context;
import android.view.View;

import com.github.curioustechizen.ago.RelativeTimeTextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import im.adamant.android.R;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import im.adamant.android.ui.messages_support.entities.FallbackMessage;
import im.adamant.android.ui.messages_support.entities.MessageListContent;
import im.adamant.android.ui.messages_support.entities.Separator;

public class SeparatorViewHolder extends AbstractMessageListContentViewHolder {
    private RelativeTimeTextView relativeTimeTextView;

    public SeparatorViewHolder(Context context, @NonNull View itemView) {
        super(context, itemView);

        relativeTimeTextView = itemView.findViewById(R.id.list_item_separator_rtv_date);
    }

    @Override
    public void bind(MessageListContent message) {
        boolean isCorruptedMessage = (message == null) || (message.getSupportedType() != SupportedMessageListContentType.SEPARATOR);
        if (isCorruptedMessage) {
            emptyView();
            return;
        }

        Separator separator = (Separator) message;

        relativeTimeTextView.setReferenceTime(separator.getTimestamp());
    }

    private void emptyView() {
        relativeTimeTextView.setReferenceTime(0);
    }
}
