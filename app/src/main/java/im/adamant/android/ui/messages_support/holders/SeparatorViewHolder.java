package im.adamant.android.ui.messages_support.holders;

import android.content.Context;
import android.view.View;

import com.github.curioustechizen.ago.RelativeTimeTextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import im.adamant.android.R;
import im.adamant.android.ui.custom_view.TodayRelativeTimeView;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import im.adamant.android.ui.messages_support.entities.FallbackMessage;
import im.adamant.android.ui.messages_support.entities.MessageListContent;
import im.adamant.android.ui.messages_support.entities.Separator;

public class SeparatorViewHolder extends AbstractMessageListContentViewHolder {
    private TodayRelativeTimeView relativeTimeTextView;
    private View leftLineView;
    private View rightLineView;

    public SeparatorViewHolder(Context context, @NonNull View itemView) {
        super(context, itemView);

        relativeTimeTextView = itemView.findViewById(R.id.list_item_separator_rtv_date);
        leftLineView = itemView.findViewById(R.id.list_item_separator_v_left);
        rightLineView = itemView.findViewById(R.id.list_item_separator_v_right);
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

        if (relativeTimeTextView.isToday()){
            relativeTimeTextView.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            leftLineView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
            rightLineView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        } else {
            relativeTimeTextView.setTextColor(ContextCompat.getColor(context, R.color.colorThird));
            leftLineView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorThird));
            rightLineView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorThird));
        }
    }

    private void emptyView() {
        relativeTimeTextView.setReferenceTime(0);
    }
}
