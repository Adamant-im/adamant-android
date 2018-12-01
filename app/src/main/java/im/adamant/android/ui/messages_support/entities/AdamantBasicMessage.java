package im.adamant.android.ui.messages_support.entities;

import android.content.Context;
import android.text.Spanned;

import im.adamant.android.helpers.AdamantAddressProcessor;
import im.adamant.android.helpers.HtmlHelper;

public class AdamantBasicMessage extends AbstractMessage {
    private String text;
    private transient Spanned htmlText;

    @Override
    public String getShortedMessage(Context context, int preferredLimit) {
       return shorteningString(text, preferredLimit);
    }

    public String getText() {
        return text;
    }

    public Spanned getHtmlText(AdamantAddressProcessor adamantAddressProcessor){
        if (htmlText == null){
            try {
                htmlText = HtmlHelper.fromHtml(adamantAddressProcessor.getHtmlString(text));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return htmlText;
    }

    public void setText(String text) {
        this.text = text;
    }
}
