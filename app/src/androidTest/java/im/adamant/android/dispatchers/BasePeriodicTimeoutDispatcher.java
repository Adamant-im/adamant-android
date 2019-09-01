package im.adamant.android.dispatchers;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.utils.AssetReaderUtil;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.SocketPolicy;

public abstract class BasePeriodicTimeoutDispatcher extends BaseDispatcher {
    private Map<String, AtomicInteger> counters = new HashMap<>();
    private Map<String, String> data;

    public BasePeriodicTimeoutDispatcher(Context context) {
        super(context);
        initializeCounters();
    }

    @Nonnull
    protected abstract Map<String, String> provideRequestPathAndResponseFile();

    protected abstract int provideAttempts();

    @Override
    protected void initializePaths(Set<String> paths) {
        data = provideRequestPathAndResponseFile();
        Set<String> providedPaths = data.keySet();
        paths.addAll(providedPaths);
    }

    protected void initializeCounters() {
        for (String path : data.keySet()) {
            counters.put(path, new AtomicInteger(0));
        }
    }

    @Override
    protected MockResponse route(String path, RecordedRequest request) {
        AtomicInteger counter = counters.get(path);

        if (counter == null) { return new MockResponse().setResponseCode(500); }

        int cntVal = counter.get();
        if (cntVal < provideAttempts()) {
            Logger.getGlobal().warning("PeriodicTimeoutDispatcher: " + (provideAttempts() - cntVal) + " Attempts for path: " + path);
            counter.incrementAndGet();
            return new MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE);
        }

        return new MockResponse()
                .setResponseCode(200)
                .setBody(AssetReaderUtil.asset(context, data.get(path)));
    }
}
