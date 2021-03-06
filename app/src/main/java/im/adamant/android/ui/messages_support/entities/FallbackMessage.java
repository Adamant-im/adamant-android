package im.adamant.android.ui.messages_support.entities;

import android.content.Context;
import android.text.Spanned;

import java.util.Locale;

import im.adamant.android.R;
import im.adamant.android.core.exceptions.MessageDecryptException;
import im.adamant.android.markdown.AdamantMarkdownProcessor;
import im.adamant.android.helpers.HtmlHelper;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;

public class FallbackMessage extends AbstractMessage {
    private String fallbackMessage;
    private String fallbackType = "none";
    private transient Spanned htmlFallBackMessage;

    public static FallbackMessage createMessageFromThrowable(Throwable throwable) {
        FallbackMessage fallbackMessage = new FallbackMessage();
        fallbackMessage.setError(throwable.getMessage());
        fallbackMessage.setSupportedType(SupportedMessageListContentType.FALLBACK);

        if (throwable instanceof MessageDecryptException) {
            fallbackMessage.setCompanionId(((MessageDecryptException) throwable).getCompanionId());
            fallbackMessage.setiSay(((MessageDecryptException) throwable).isISay());
            fallbackMessage.setTimestamp(((MessageDecryptException) throwable).getTimestamp());
            fallbackMessage.setStatus(AbstractMessage.Status.INVALIDATED);
            fallbackMessage.setTransactionId(((MessageDecryptException) throwable).getTransactionId());
        }
        return fallbackMessage;
    }

    @Override
    public String getShortedMessage(Context context, int preferredLimit) {
        if (fallbackMessage == null || fallbackMessage.isEmpty()){
            fallbackMessage = String.format(Locale.ENGLISH, context.getString(R.string.unsupported_message_type), fallbackType);
        }

        return fallbackMessage;
    }

    public String getFallbackMessage() {
        return fallbackMessage;
    }

    public Spanned getHtmlFallBackMessage(AdamantMarkdownProcessor adamantAddressProcessor) {
        if (htmlFallBackMessage == null) {
            try {
                htmlFallBackMessage = HtmlHelper.fromHtml(adamantAddressProcessor.getHtmlString(fallbackMessage));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return htmlFallBackMessage;
    }

    public void setFallbackMessage(String fallbackMessage) {
        this.fallbackMessage = fallbackMessage;
    }

    public String getFallbackType() {
        return fallbackType;
    }

    public void setFallbackType(String fallbackType) {
        this.fallbackType = fallbackType;
    }
}
