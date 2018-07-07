package im.adamant.android.ui.holders.messages;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import im.adamant.android.R;
import im.adamant.android.helpers.HtmlHelper;
import im.adamant.android.ui.entities.messages.AbstractMessage;
import im.adamant.android.ui.entities.messages.AdamantBasicMessage;
import im.adamant.android.ui.entities.messages.FallbackMessage;
import im.adamant.android.ui.messages_support.SupportedMessageTypes;

import com.github.curioustechizen.ago.RelativeTimeTextView;

import java.text.Format;
import java.util.Locale;


public class AdamantBasicMessageViewHolder extends AbstractMessageViewHolder {
    private ImageView processedView;
    private TextView messageView;
    private RelativeTimeTextView dateView;

    public AdamantBasicMessageViewHolder(Context context, View itemView) {
        super(context, itemView);

        processedView = itemView.findViewById(R.id.list_item_message_processed);
        messageView = itemView.findViewById(R.id.list_item_message_text);
        dateView = itemView.findViewById(R.id.list_item_message_date);
    }

    @Override
    public void bind(AbstractMessage message) {

        if (message != null){

            if (message.getSupportedType() != SupportedMessageTypes.ADAMANT_BASIC){
                emptyView();
                return;
            }

            AdamantBasicMessage basicMessage = (AdamantBasicMessage)message;

            messageView.setText(
                    HtmlHelper.fromHtml(
                            basicMessage.getText()
                    )
            );

            dateView.setReferenceTime(message.getDate());

            if (message.isProcessed()){
                processedView.setImageResource(R.drawable.ic_processed);
            } else {
                processedView.setImageResource(R.drawable.ic_not_processed);
            }

            if (message.isiSay()){
                iSayedLayoutModification();
            } else {
                companionSayedModification();
            }

        } else {
            emptyView();
        }
    }

    private void iSayedLayoutModification(){
        processedView.setVisibility(View.VISIBLE);
    }

    private void companionSayedModification(){
        processedView.setVisibility(View.GONE);
    }

    private void emptyView() {
        messageView.setText("");
        processedView.setImageResource(R.drawable.ic_not_processed);
        dateView.setReferenceTime(System.currentTimeMillis());
    }
}
