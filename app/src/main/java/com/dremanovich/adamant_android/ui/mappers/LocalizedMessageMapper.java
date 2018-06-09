package com.dremanovich.adamant_android.ui.mappers;

import android.content.Context;

import com.dremanovich.adamant_android.R;
import com.dremanovich.adamant_android.ui.entities.Message;
import io.reactivex.functions.Function;

public class LocalizedMessageMapper implements Function<Message, Message> {
    private static final String WELCOME_MESSAGE_ADDR = "U15423595369615486571";
    private static final String MESSAGE_CTNCR_ADDR = "U7047165086065693428";

    private Context context;

    public LocalizedMessageMapper(Context context) {
        this.context = context;
    }

    @Override
    public Message apply(Message message) {
        String messageText = message.getMessage();

        switch (message.getCompanionId()){
            case WELCOME_MESSAGE_ADDR: {
                messageText = context.getString(R.string.hello_message_baunty);
            }
            break;
            case MESSAGE_CTNCR_ADDR: {
                messageText = context.getString(R.string.hello_message_ico);
            }
            break;
        }

        message.setMessage(messageText);

        return message;
    }
}
