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
    public Chat apply(Chat chat) throws Exception {
        String chatTitle = chat.getCompanionId();

        switch (chat.getCompanionId()){
            case BuildConfig.WELCOME_MESSAGE_ADDR: {
                chatTitle = context.getString(R.string.hello_message_baunty_name);
            }
            break;
            case BuildConfig.MESSAGE_CTNCR_ADDR: {
                chatTitle = context.getString(R.string.hello_message_ico_name);
            }
            break;
        }

        chat.setTitle(chatTitle);

        return chat;
    }
}
