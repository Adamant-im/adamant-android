package im.adamant.android.ui.mappers;

import android.content.Context;

import im.adamant.android.BuildConfig;
import im.adamant.android.R;
import im.adamant.android.ui.entities.Message;
import io.reactivex.functions.Function;

public class LocalizedMessageMapper implements Function<Message, Message> {

    private Context context;

    public LocalizedMessageMapper(Context context) {
        this.context = context;
    }

    @Override
    public Message apply(Message message) {
        String messageText = message.getMessage();

        switch (message.getCompanionId()){
            case BuildConfig.WELCOME_MESSAGE_ADDR: {
                messageText = context.getString(R.string.hello_message_baunty);
            }
            break;
            case BuildConfig.MESSAGE_CTNCR_ADDR: {
                messageText = context.getString(R.string.hello_message_ico);
            }
            break;
        }

        message.setMessage(messageText);

        return message;
    }
}
