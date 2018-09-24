package im.adamant.android.ui.messages_support.factories;

import java.util.HashMap;
import java.util.Map;

import im.adamant.android.ui.messages_support.SupportedMessageType;

public class MessageFactoryProvider {
    private Map<SupportedMessageType, MessageFactory> factories = new HashMap<>();

    public MessageFactory getFactoryByType(SupportedMessageType type) throws Exception {
        if (type == null || type == SupportedMessageType.UNDEFINED){
            throw new Exception("Not implemented!");
        }

        if (!factories.containsKey(type)){
            throw new Exception("Type has not registered!");
        }

        return factories.get(type);
    }

    public void registerFactory(SupportedMessageType type, MessageFactory factory){
        factories.put(type, factory);
    }
}
