package im.adamant.android.base;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.intent.Intents;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import im.adamant.android.ClosableApplication;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockWebServer;

public abstract class ActivityWithMockingNetworkUITest<T extends Activity> {
    @Rule
    public final ActivityTestRule<T> activityRule;

    @Rule
    public final TestName testNameRule = new TestName();

    private Set<IdlingResource> registeredResources = new HashSet<>();

    protected MockWebServer mockWebServer = new MockWebServer();

    public ActivityWithMockingNetworkUITest() {
        activityRule = new ActivityTestRule<>(provideActivityClass(), true, false);
    }

    protected abstract Class<T> provideActivityClass();

    protected abstract Dispatcher provideDispatcher(String testName);

    @Before
    public void setup() throws IOException {
        mockWebServer.start(8080);
        mockWebServer.setDispatcher(provideDispatcher(testNameRule.getMethodName()));

        Intents.init();

        activityRule.launchActivity(null);
    }

    @After
    public void teardown() throws IOException {
        for (IdlingResource resource : registeredResources) {
            IdlingRegistry.getInstance().unregister(resource);
        }
        activityRule.finishActivity();

        Intents.release();
        mockWebServer.shutdown();

        ClosableApplication application = (ClosableApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        application.close();

    }

    protected void idlingBlock(IdlingResource idlingResource, Runnable runnable) {
        IdlingRegistry.getInstance().register(idlingResource);
        registeredResources.add(idlingResource);
        runnable.run();
    }
}
