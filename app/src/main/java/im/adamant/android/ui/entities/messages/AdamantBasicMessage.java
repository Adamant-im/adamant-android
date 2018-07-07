package im.adamant.android.ui.entities.messages;

import android.content.Context;

public class AdamantBasicMessage extends AbstractMessage {
    private String text;

    @Override
    public String getShortedMessage(Context context, int preferredLimit) {
       return shorteningString(text, preferredLimit);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
