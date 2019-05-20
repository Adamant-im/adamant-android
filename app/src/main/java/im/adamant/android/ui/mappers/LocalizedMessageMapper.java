package im.adamant.android.ui.mappers;

import android.content.Context;

import im.adamant.android.BuildConfig;
import im.adamant.android.R;
import im.adamant.android.core.AdamantApi;
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
            switch (message.getCompanionId()){
                case BuildConfig.WELCOME_MESSAGE_ADDR: {
                    return null;
                }
                case BuildConfig.MESSAGE_CTNCR_ADDR: {
                    return null;
                }
            }
        }

        return message;
    }

    public AbstractMessage buildMessage(String address) {
        AdamantBasicMessage basicMessage = new AdamantBasicMessage();
        basicMessage.setCompanionId(address);
        basicMessage.setiSay(false);
        basicMessage.setTimestamp(AdamantApi.BASE_TIMESTAMP);
        basicMessage.setSupportedType(SupportedMessageListContentType.ADAMANT_BASIC);
        basicMessage.setStatus(AbstractMessage.Status.DELIVERED);
        basicMessage.setText(context.getString(R.string.hello_message_baunty));
        basicMessage.setTransactionId("HL0001"); // just unique id. May be any.

        return basicMessage;
    }
}
