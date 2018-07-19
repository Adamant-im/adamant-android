package im.adamant.android.ui.entities.messages;

import android.content.Context;
import android.text.Spanned;

import java.util.Locale;

import im.adamant.android.R;
import im.adamant.android.helpers.AdamantAddressProcessor;
import im.adamant.android.helpers.HtmlHelper;

public class FallbackMessage extends AbstractMessage {
    private String fallbackMessage;
    private String fallbackType = "none";
    private String shortedMessage;
    private transient Spanned htmlFallBackMessage;

    @Override
    public String getShortedMessage(Context context, int preferredLimit) {

        shortedMessage = shorteningString(fallbackMessage, preferredLimit);

        if (shortedMessage == null || shortedMessage.isEmpty()){
            shortedMessage = String.format(Locale.ENGLISH, context.getString(R.string.unsupported_message_type), fallbackType);
            shortedMessage = shorteningString(shortedMessage, preferredLimit);
        }

        return shortedMessage;
    }

    public String getFallbackMessage() {
        return fallbackMessage;
    }

    public Spanned getHtmlFallBackMessage(AdamantAddressProcessor adamantAddressProcessor) {
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
