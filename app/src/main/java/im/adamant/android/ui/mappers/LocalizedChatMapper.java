package im.adamant.android.ui.mappers;

import android.content.Context;

import im.adamant.android.BuildConfig;
import im.adamant.android.R;
import im.adamant.android.ui.entities.Chat;
import io.reactivex.functions.Function;

public class LocalizedChatMapper implements Function<Chat, Chat> {
    private Context context;

    public LocalizedChatMapper(Context context) {
        this.context = context;
    }

    @Override
    public Chat apply(Chat chat) {
        switch (chat.getCompanionId()){
            case BuildConfig.WELCOME_MESSAGE_ADDR: {
                chat.setTitle(context.getString(R.string.hello_message_baunty_name));
            }
            break;
            case BuildConfig.MESSAGE_CTNCR_ADDR: {
                return null;
            }
        }

        return chat;
    }

    public Chat buildChat(String address) {
        Chat chat = new Chat();
        chat.setCompanionId(address);
        return apply(chat);
    }
}
