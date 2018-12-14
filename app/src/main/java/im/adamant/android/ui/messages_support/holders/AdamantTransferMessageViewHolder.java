package im.adamant.android.ui.messages_support.holders;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import im.adamant.android.R;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.helpers.AdamantAddressProcessor;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.entities.AdamantTransferMessage;
import im.adamant.android.ui.messages_support.entities.EthereumTransferMessage;
import im.adamant.android.ui.messages_support.entities.MessageListContent;

public class AdamantTransferMessageViewHolder extends AbstractMessageViewHolder {
    private TextView amountView;
    private ImageView processedView;
    private View contentView;

    public AdamantTransferMessageViewHolder(Context context, View v, AdamantAddressProcessor adamantAddressProcessor, Avatar avatar) {
        super(context, v, adamantAddressProcessor, avatar);

        LayoutInflater inflater = LayoutInflater.from(context);
        contentView = inflater.inflate(R.layout.list_subitem_adamant_transfer_message, contentBlock, false);
        contentBlock.addView(contentView);

        processedView = contentView.findViewById(R.id.list_item_message_processed);
        amountView = contentView.findViewById(R.id.list_item_message_amount);
    }

    @Override
    public void bind(MessageListContent message) {
        boolean isCorruptedMessage = (message == null) || (message.getSupportedType() != SupportedMessageListContentType.ADAMANT_TRANSFER_MESSAGE);

        if (isCorruptedMessage) {
            emptyView();
            return;
        }

        super.bind(message);

        AdamantTransferMessage ethereumTransferMessage = (AdamantTransferMessage) message;


        String amountText = String.format(Locale.ENGLISH, "%.3f", ethereumTransferMessage.getAmount()) + " " +
                context.getResources().getString(R.string.adm_currency_abbr);

        amountView.setText(amountText);

        displayProcessedStatus(processedView, ethereumTransferMessage);
    }
}
