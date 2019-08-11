package im.adamant.android.dispatchers;

import android.content.Context;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import im.adamant.android.utils.AssetReaderUtil;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

public class SuccessAuthorizeDispatcher extends Dispatcher {
    private Context context;
    private Map<String, String> responseFilesByPath = new HashMap<>();

    public SuccessAuthorizeDispatcher(Context context) {
        this.context = context;
        responseFilesByPath.put("/api/accounts", "account/get_0_01_balance_account.json");
        responseFilesByPath.put("/api/transactions", "transactions/transactions.json");
    }

    @Override
    public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
        MockResponse errorResponse = new MockResponse()
                .setResponseCode(404)
                .setHeadersDelay(20, TimeUnit.MILLISECONDS);

        Uri parsed = Uri.parse(request.getPath());
        if (parsed == null) { return errorResponse; }
        String pathWithoutQueryParams = parsed.getPath();
        String responseFile = responseFilesByPath.get(pathWithoutQueryParams);

        if (responseFile != null) {
            String responseBody = AssetReaderUtil.asset(context, responseFile);
            return new MockResponse()
                    .setResponseCode(200)
                    .setBody(responseBody)
                    .setBodyDelay(200, TimeUnit.MILLISECONDS)
                    .setHeadersDelay(20, TimeUnit.MILLISECONDS);
        } else {
            return errorResponse;
        }
    }
}
