package im.adamant.android.ui.messages_support.factories;

import java.util.HashMap;
import java.util.Map;

import im.adamant.android.ui.messages_support.SupportedMessageTypes;

public class MessageFactoryProvider {
    private Map<SupportedMessageTypes, MessageFactory> factories = new HashMap<>();

    public MessageFactory getFactoryByType(SupportedMessageTypes type) throws Exception {
        if (type == null || type == SupportedMessageTypes.UNDEFINED){
            throw new Exception("Not implemented!");
        }

        if (!factories.containsKey(type)){
            throw new Exception("Type has not registered!");
        }

        return factories.get(type);
    }

    public void registerFactory(SupportedMessageTypes type, MessageFactory factory){
        factories.put(type, factory);
    }
}
