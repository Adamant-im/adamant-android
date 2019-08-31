package im.adamant.android.base;

import android.app.Activity;

import androidx.test.espresso.intent.Intents;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

import java.io.IOException;

import im.adamant.android.AdamantApplication;
import im.adamant.android.InstrumentationTestFacade;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockWebServer;

public abstract class ActivityWithMockingNetworkUITest<T extends Activity> extends BaseTest<T> {

    protected MockWebServer mockWebServer = new MockWebServer();

    protected abstract Dispatcher provideDispatcher(String testName);

    @Before
    public void setup() throws IOException {
        Dispatcher dispatcher = provideDispatcher(testNameRule.getMethodName());
        if (dispatcher != null) {
            mockWebServer.setDispatcher(dispatcher);
        }
        mockWebServer.start(8080);

        super.setup();

        Intents.init();
    }

    @After
    public void teardown() throws IOException {
        InstrumentationTestFacade instrumentationTestFacade = application.getInstrumentationTestFacade();
        instrumentationTestFacade.logout();

        super.teardown();

        Intents.release();
        mockWebServer.shutdown();
    }
}
