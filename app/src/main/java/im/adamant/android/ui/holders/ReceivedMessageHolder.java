package im.adamant.android.ui.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import im.adamant.android.R;
import im.adamant.android.ui.entities.Message;
import com.github.curioustechizen.ago.RelativeTimeTextView;

public class ReceivedMessageHolder extends RecyclerView.ViewHolder {
    private TextView messageView;
    private RelativeTimeTextView dateView;

    public ReceivedMessageHolder(View itemView) {
        super(itemView);

        messageView = itemView.findViewById(R.id.list_item_message_text);
        dateView = itemView.findViewById(R.id.list_item_message_date);
    }

    public void bind(Message message){
        if (message != null){
            messageView.setText(message.getMessage());
            dateView.setReferenceTime(message.getDate());
        } else {
            messageView.setText("");
            dateView.setReferenceTime(System.currentTimeMillis());
        }
    }
}
