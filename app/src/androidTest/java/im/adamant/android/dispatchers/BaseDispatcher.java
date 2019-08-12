package im.adamant.android.dispatchers;

import android.content.Context;
import android.net.Uri;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import im.adamant.android.utils.AssetReaderUtil;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

public abstract class BaseDispatcher extends Dispatcher {
    protected Context context;
    private Set<String> paths = new HashSet<>();

    public BaseDispatcher(Context context) {
        this.context = context;
        initializePaths(paths);
    }

    protected abstract void initializePaths(Set<String> paths);

    @Override
    public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
        Uri parsed = Uri.parse(request.getPath());
        if (parsed == null) { return error(404); }
        String pathWithoutQueryParams = parsed.getPath();
        boolean isExistedPath = paths.contains(pathWithoutQueryParams);

        if (isExistedPath) {
            MockResponse response = route(pathWithoutQueryParams, request);
            boolean withoutTimeout = (response.getBodyDelay(TimeUnit.MILLISECONDS) == 0);

            if (withoutTimeout) {
                response = fastResponse(response);
            }

            return response;
        } else {
            return error(404);
        }
    }

    protected abstract MockResponse route(String path, RecordedRequest request);

    protected MockResponse fastResponse(MockResponse response) {
        return response
                .setBodyDelay(200, TimeUnit.MILLISECONDS)
                .setHeadersDelay(20, TimeUnit.MILLISECONDS);
    }

    protected MockResponse error(int code) {
        return new MockResponse()
                .setResponseCode(code)
                .setHeadersDelay(20, TimeUnit.MILLISECONDS);
    }
}
