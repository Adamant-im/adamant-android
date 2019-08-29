package im.adamant.android.dispatchers;

import android.content.Context;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import im.adamant.android.utils.AssetReaderUtil;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.SocketPolicy;

public class ErroneousChatDispatcher extends BaseDispatcher {
    private static final int ATTEMPTS = 3;
    private static final String CHATROOMS_PATH = "/api/chatrooms/U15650841420785452182";
    private AtomicInteger counter = new AtomicInteger(0);

    public ErroneousChatDispatcher(Context context) {
        super(context);
    }

    @Override
    protected void initializePaths(Set<String> paths) {
        paths.add(CHATROOMS_PATH);
    }

    @Override
    protected MockResponse route(String path, RecordedRequest request) {
        int cntVal = counter.get();
        if (cntVal < ATTEMPTS) {
            counter.incrementAndGet();
            return new MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE);
        }

        switch (path) {
            case CHATROOMS_PATH: {
                return new MockResponse()
                        .setResponseCode(200)
                        .setBody(AssetReaderUtil.asset(context, "chats/correct_chat_list.json"));
            }
            default:
                return null;
        }
    }
}
