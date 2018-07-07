package im.adamant.android.ui.holders.messages;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;

import java.util.Locale;

import im.adamant.android.R;
import im.adamant.android.helpers.HtmlHelper;
import im.adamant.android.ui.entities.messages.AbstractMessage;
import im.adamant.android.ui.entities.messages.AdamantBasicMessage;
import im.adamant.android.ui.entities.messages.FallbackMessage;
import im.adamant.android.ui.messages_support.SupportedMessageTypes;

public class FallbackMessageViewHolder extends AbstractMessageViewHolder {
    private ImageView processedView;
    private TextView messageView;
    private RelativeTimeTextView dateView;

    public FallbackMessageViewHolder(Context context, View v) {
        super(context, v);

        processedView = itemView.findViewById(R.id.list_item_message_processed);
        messageView = itemView.findViewById(R.id.list_item_message_text);
        dateView = itemView.findViewById(R.id.list_item_message_date);
    }

    @Override
    public void bind(AbstractMessage message) {
        if (message != null){

            if (message.getSupportedType() != SupportedMessageTypes.FALLBACK){
                emptyView();
                return;
            }

            FallbackMessage fallbackMessage = (FallbackMessage)message;

            messageView.setText(
                    HtmlHelper.fromHtml(
                            resolveFallbackMessage(fallbackMessage)
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

    private String resolveFallbackMessage(FallbackMessage message) {
        String messageText = message.getFallbackMessage();
        if (message.getFallbackMessage() == null || message.getFallbackMessage().isEmpty()){
            messageText = String.format(
                    Locale.ENGLISH,
                    context.getString(R.string.unsupported_message_type),
                    message.getFallbackType()
            );
        }

        return messageText;
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
