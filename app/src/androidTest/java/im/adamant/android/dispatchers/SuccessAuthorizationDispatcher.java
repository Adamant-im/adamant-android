package im.adamant.android.dispatchers;

import android.content.Context;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import im.adamant.android.utils.AssetReaderUtil;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

public class SuccessAuthorizationDispatcher extends BaseDispatcher {
    public static final String AUTHORIZATION_PATH = "/api/accounts";
    public static final String TRANSACTIONS_PATH = "/api/transactions";

    public SuccessAuthorizationDispatcher(Context context) {
        super(context);
    }

    @Override
    protected void initializePaths(Set<String> paths) {
        paths.add(AUTHORIZATION_PATH);
        paths.add(TRANSACTIONS_PATH);
    }

    @Override
    protected MockResponse route(String path, RecordedRequest request) {
        switch (path) {
            case AUTHORIZATION_PATH : {
                return new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                AssetReaderUtil.asset(context, "accounts/get_0_01_balance_account.json")
                        );
            }
            //TODO: If transactions not provide test hungup. Solve this problem.
            case TRANSACTIONS_PATH : {
                return new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                AssetReaderUtil.asset(context, "transactions/transactions.json")
                        );
            }
        }

        return error(404);
    }
}
