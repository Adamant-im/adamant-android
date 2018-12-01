package im.adamant.android.ui.messages_support.entities;

import android.content.Context;

public class AdamantPushSubscriptionMessage extends AbstractMessage {
    private String token;
    private String provider;

    @Override
    public String getShortedMessage(Context context, int preferredLimit) {
        return "";
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
