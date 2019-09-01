package im.adamant.android.dispatchers;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import static im.adamant.android.utils.InstrumentedTestConstants.CHATROOMS_PATH;
import static im.adamant.android.utils.InstrumentedTestConstants.CONTACTS_PATH;

public class ContactsListPeriodicTimeoutDispatcher extends BasePeriodicTimeoutDispatcher {

    public ContactsListPeriodicTimeoutDispatcher(Context context) {
        super(context);
    }

    @Nonnull
    @Override
    protected Map<String, String> provideRequestPathAndResponseFile() {
        Map<String, String> data = new HashMap<>();
        data.put(CHATROOMS_PATH, "chats/correct_chat_list.json");
        data.put(CONTACTS_PATH, "kvs/contact_list.json");

        return data;
    }

    @Override
    protected int provideAttempts() {
        return 1;
    }
}
