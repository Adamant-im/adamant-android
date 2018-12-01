package im.adamant.android.ui.mappers;

import android.content.Context;

import im.adamant.android.BuildConfig;
import im.adamant.android.R;
import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import im.adamant.android.ui.messages_support.entities.AdamantBasicMessage;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import io.reactivex.functions.Function;

public class LocalizedMessageMapper implements Function<AbstractMessage, AbstractMessage> {

    private Context context;

    public LocalizedMessageMapper(Context context) {
        this.context = context;
    }

    @Override
    public AbstractMessage apply(AbstractMessage message) {

        if (message.getSupportedType() == SupportedMessageListContentType.ADAMANT_BASIC){
            AdamantBasicMessage basicMessage = (AdamantBasicMessage) message;
            String messageText = basicMessage.getText();

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

            basicMessage.setText(messageText);
        }

        return message;
    }
}
