package im.adamant.android.ui.messages_support.holders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;

import java.util.Locale;

import im.adamant.android.R;
import im.adamant.android.helpers.AdamantAddressProcessor;
import im.adamant.android.ui.messages_support.entities.EthereumTransferMessage;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.entities.MessageListContent;

public class EthereumTransferMessageViewHolder extends AbstractMessageListContentViewHolder {
    private ImageView processedView;
    private TextView messageView;
    private TextView amountView;
    private RelativeTimeTextView dateView;
    private AdamantAddressProcessor adamantAddressProcessor;

    public EthereumTransferMessageViewHolder(Context context, View v, AdamantAddressProcessor adamantAddressProcessor) {
        super(context, v);

        this.adamantAddressProcessor = adamantAddressProcessor;

        processedView = itemView.findViewById(R.id.list_item_message_processed);
        messageView = itemView.findViewById(R.id.list_item_message_text);
        dateView = itemView.findViewById(R.id.list_item_message_date);
        amountView = itemView.findViewById(R.id.list_item_message_amount);
    }

    @Override
    public void bind(MessageListContent message) {
        if (message != null){

            if (message.getSupportedType() != SupportedMessageListContentType.ETHEREUM_TRANSFER){
                emptyView();
                return;
            }

            EthereumTransferMessage ethereumTransferMessage = (EthereumTransferMessage)message;

            messageView.setText(
                    ethereumTransferMessage.getHtmlComment(adamantAddressProcessor)
            );

            amountView.setText(String.format(Locale.ENGLISH, "%.8f", ethereumTransferMessage.getAmount()));

            dateView.setReferenceTime(ethereumTransferMessage.getTimestamp());

            if (ethereumTransferMessage.isProcessed()){
                processedView.setImageResource(R.drawable.ic_processed);
            } else {
                processedView.setImageResource(R.drawable.ic_not_processed);
            }

            if (ethereumTransferMessage.isiSay()){
                iSayedLayoutModification();
            } else {
                companionSayedModification();
            }

        } else {
            emptyView();
        }
    }

    private String resolveMessage(EthereumTransferMessage message) {
        String messageText = "";

        if (message.isiSay()){
            messageText += "<--";
        } else {
            messageText += "-->";
        }

        messageText += " eth: " + message.getAmount().toString() + "\n<br/>" + message.getComment();

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
