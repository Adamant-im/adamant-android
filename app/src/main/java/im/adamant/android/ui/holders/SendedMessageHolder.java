package im.adamant.android.ui.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import im.adamant.android.R;
import im.adamant.android.ui.entities.Message;
import com.github.curioustechizen.ago.RelativeTimeTextView;

import java.util.Locale;

//TODO: Merge with ReceivedMessageHolder
public class SendedMessageHolder extends RecyclerView.ViewHolder {
    private Context context;

    private ImageView processedView;
    private TextView messageView;
    private RelativeTimeTextView dateView;

    public SendedMessageHolder(Context context, View itemView) {
        super(itemView);

        this.context = context;

        processedView = itemView.findViewById(R.id.list_item_message_processed);
        messageView = itemView.findViewById(R.id.list_item_message_text);
        dateView = itemView.findViewById(R.id.list_item_message_date);
    }

    public void bind(Message message){
        if (message != null){
            messageView.setText(
                Html.fromHtml(
                    extractMessageText(message)
                )
            );

            dateView.setReferenceTime(message.getDate());

            if (message.isProcessed()){
                processedView.setImageResource(R.drawable.ic_processed);
            } else {
                processedView.setImageResource(R.drawable.ic_not_processed);
            }

        } else {
            messageView.setText("");
            processedView.setImageResource(R.drawable.ic_not_processed);
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
