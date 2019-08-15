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

public abstract class ActivityWithMockingNetworkUITest<T extends Activity> extends BaseTest {
    @Rule
    public final ActivityTestRule<T> activityRule;

    @Rule
    public final TestName testNameRule = new TestName();

    protected MockWebServer mockWebServer = new MockWebServer();

    public ActivityWithMockingNetworkUITest() {
        activityRule = new ActivityTestRule<>(provideActivityClass(), true, false);
    }

    protected abstract Class<T> provideActivityClass();

    protected abstract Dispatcher provideDispatcher(String testName);

    @Before
    public void setup() throws IOException {
        super.setup();
        Dispatcher dispatcher = provideDispatcher(testNameRule.getMethodName());
        if (dispatcher != null) {
            mockWebServer.setDispatcher(dispatcher);
        }
        mockWebServer.start(8080);

        Intents.init();

        activityRule.launchActivity(null);
    }

    @After
    public void teardown() throws IOException {
        super.teardown();
        activityRule.finishActivity();

        Intents.release();
        mockWebServer.shutdown();

        AdamantApplication application = (AdamantApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        InstrumentationTestFacade instrumentationTestFacade = application.getInstrumentationTestFacade();
        instrumentationTestFacade.logout();

    }
}
