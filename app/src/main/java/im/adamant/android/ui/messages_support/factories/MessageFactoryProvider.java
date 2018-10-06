package im.adamant.android.ui.messages_support.factories;

import java.util.HashMap;
import java.util.Map;

import im.adamant.android.ui.messages_support.SupportedMessageListContentType;

public class MessageFactoryProvider {
    private Map<SupportedMessageListContentType, MessageFactory> factories = new HashMap<>();

    public MessageFactory getFactoryByType(SupportedMessageListContentType type) throws Exception {
        if (type == null || type == SupportedMessageListContentType.UNDEFINED){
            throw new Exception("Not implemented!");
        }

        if (!factories.containsKey(type)){
            throw new Exception("Type has not registered!");
        }

        return factories.get(type);
    }

    public void registerFactory(SupportedMessageListContentType type, MessageFactory factory){
        factories.put(type, factory);
    }
}
