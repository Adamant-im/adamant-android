package im.adamant.android.ui.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import im.adamant.android.R;
import im.adamant.android.ui.entities.Message;
import com.github.curioustechizen.ago.RelativeTimeTextView;

import java.util.Locale;

public class ReceivedMessageHolder extends RecyclerView.ViewHolder {
    private Context context;

    private TextView messageView;
    private RelativeTimeTextView dateView;

    public ReceivedMessageHolder(Context context, View itemView) {
        super(itemView);

        this.context = context;

        messageView = itemView.findViewById(R.id.list_item_message_text);
        dateView = itemView.findViewById(R.id.list_item_message_date);
    }

    public void bind(Message message){
        if (message != null){
            //TODO: Html.fromHtml was deprecated in API >= 24
            messageView.setText(
                    Html.fromHtml(
                        extractMessageText(message)
                    )
            );
            dateView.setReferenceTime(message.getDate());
        } else {
            messageView.setText("");
            dateView.setReferenceTime(System.currentTimeMillis());
        }
    }

    private String extractMessageText(Message message){
        String text = message.getMessage();

        if (message.isFallback()){
            text = message.getFallBackMessage();

            if (text.isEmpty()){
                text = String.format(Locale.ENGLISH, context.getString(R.string.unsupported_message_type), message.getReachType());
            }
        }

        return text;
    }
}
