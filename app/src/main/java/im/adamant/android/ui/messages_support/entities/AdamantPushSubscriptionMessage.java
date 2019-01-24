package im.adamant.android.ui.messages_support.entities;

import android.content.Context;

import com.google.gson.annotations.Expose;

public class AdamantPushSubscriptionMessage extends AbstractMessage {
    public static final String ADD_ACTION = "add";
    public static final String REMOVE_ACTION = "remove";

    @Expose
    private String token;

    @Expose
    private String provider;

    @Expose
    private String action;

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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
