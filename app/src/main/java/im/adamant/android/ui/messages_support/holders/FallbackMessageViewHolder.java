package im.adamant.android.ui.messages_support.holders;

import android.content.Context;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import im.adamant.android.R;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.markdown.AdamantMarkdownProcessor;
import im.adamant.android.helpers.HtmlHelper;
import im.adamant.android.ui.messages_support.entities.FallbackMessage;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.entities.MessageListContent;

public class FallbackMessageViewHolder extends AbstractMessageViewHolder {
    private TextView messageView;
    private View contentView;

    public FallbackMessageViewHolder(Context context, View v, AdamantMarkdownProcessor adamantAddressProcessor, Avatar avatar) {
        super(context, v, adamantAddressProcessor, avatar);

        LayoutInflater inflater = LayoutInflater.from(context);
        contentView = inflater.inflate(R.layout.list_subitem_fallback_message, contentBlock, false);
        contentBlock.addView(contentView);

        messageView = contentView.findViewById(R.id.list_item_message_text);
        messageView.setMovementMethod(LinkMovementMethod.getInstance());

    }

    @Override
    public void bind(MessageListContent message, boolean isNextMessageWithSameSender, boolean isLastMessage) {
        boolean isCorruptedMessage = (message == null) || (message.getSupportedType() != SupportedMessageListContentType.FALLBACK);

        if (isCorruptedMessage) {
            emptyView();
            return;
        }

        super.bind(message, isNextMessageWithSameSender, isLastMessage);

        FallbackMessage fallbackMessage = (FallbackMessage) message;
        Spanned messageText = fallbackMessage.getHtmlFallBackMessage(adamantAddressProcessor);
        if (messageText == null || messageText.length() == 0){
            messageText = HtmlHelper.fromHtml(resolveFallbackMessage(fallbackMessage));
        }

        messageView.setText(messageText);

        displayProcessedStatus(fallbackMessage);
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

}
