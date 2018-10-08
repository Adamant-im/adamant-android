package im.adamant.android.ui.messages_support.holders;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import im.adamant.android.R;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.helpers.AdamantAddressProcessor;
import im.adamant.android.ui.messages_support.entities.AdamantBasicMessage;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.entities.MessageListContent;


public class AdamantBasicMessageViewHolder extends AbstractMessageViewHolder {
    private TextView messageView;
    private ImageView processedView;
    private View contentView;

    public AdamantBasicMessageViewHolder(
            Context context,
            View itemView,
            AdamantAddressProcessor adamantAddressProcessor,
            Avatar avatar
    ) {
        super(context, itemView, adamantAddressProcessor, avatar);

        LayoutInflater inflater = LayoutInflater.from(context);
        contentView = inflater.inflate(R.layout.list_subitem_adamant_basic_message, contentBlock, false);
        contentBlock.addView(contentView);

        processedView = contentView.findViewById(R.id.list_item_message_processed);

        messageView = contentView.findViewById(R.id.list_item_message_text);
        messageView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void bind(MessageListContent message) {
        boolean isCorruptedMessage = (message == null) || (message.getSupportedType() != SupportedMessageListContentType.ADAMANT_BASIC);

        if (isCorruptedMessage) {
            emptyView();
            return;
        }

        super.bind(message);

        AdamantBasicMessage basicMessage = (AdamantBasicMessage) message;

        messageView.setText(
                basicMessage.getHtmlText(adamantAddressProcessor)
        );

        displayProcessedStatus(processedView, basicMessage);
    }


}
