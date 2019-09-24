package im.adamant.android.dispatchers;

import android.content.Context;

import java.util.Set;

import im.adamant.android.utils.AssetReaderUtil;
import im.adamant.android.utils.InstrumentedTestConstants;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

public class ServerDownOnSendMessageDispatcher extends BaseDispatcher {

    public ServerDownOnSendMessageDispatcher(Context context) {
        super(context);
    }

    @Override
    protected void initializePaths(Set<String> paths) {
        paths.add(InstrumentedTestConstants.NORMALIZE_PATH);
        paths.add(InstrumentedTestConstants.PROCESS_PATH);
        paths.add(InstrumentedTestConstants.ACCOUNT_PATH);
    }

    @Override
    protected MockResponse route(String path, RecordedRequest request) {
        if (InstrumentedTestConstants.ACCOUNT_PATH.equalsIgnoreCase(path)) {
            return new MockResponse()
                    .setResponseCode(200)
                    .setBody(
                        AssetReaderUtil.asset(context, "accounts/get_0_01_balance_account.json")
                    );
        } else {
            return new MockResponse().setResponseCode(500);
        }
    }
}
