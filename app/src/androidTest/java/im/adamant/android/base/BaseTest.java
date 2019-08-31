package im.adamant.android.base;

import android.app.Activity;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.goterl.lazycode.lazysodium.utils.KeyPair;

import org.junit.Rule;
import org.junit.rules.TestName;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import im.adamant.android.AdamantApplication;
import im.adamant.android.InstrumentationTestFacade;
import im.adamant.android.core.entities.Account;
import im.adamant.android.utils.InstrumentedTestConstants;

public abstract class BaseTest<T extends Activity> {

    @Rule
    public final ActivityTestRule<T> activityRule;

    @Rule
    public final TestName testNameRule = new TestName();

    private Set<IdlingResource> registeredResources = new HashSet<>();
    protected AdamantApplication application;

    public BaseTest() {
        activityRule = new ActivityTestRule<>(provideActivityClass(), true, false);
    }

    protected abstract boolean isProtectedScreen();
    protected abstract void startActivity(String testName);
    protected abstract Class<T> provideActivityClass();

    protected void idlingBlock(IdlingResource idlingResource, Exceptionable runnable) throws Exception {
        IdlingRegistry.getInstance().register(idlingResource);
        registeredResources.add(idlingResource);
        runnable.run();
        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    public void setup() throws IOException {
        application = (AdamantApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        if (isProtectedScreen()) {
            InstrumentationTestFacade instrumentationTestFacade = application.getInstrumentationTestFacade();

            Account account = new Account();
            account.setAddress(InstrumentedTestConstants.ADDRESS);
            account.setBalance(InstrumentedTestConstants.DEFAULT_BALANCE);
            account.setUnconfirmedBalance(InstrumentedTestConstants.DEFAULT_BALANCE);
            account.setPublicKey(InstrumentedTestConstants.PUBLIC_KEY);

            KeyPair keyPair = new KeyPair(
                    InstrumentedTestConstants.PUBLIC_KEY,
                    InstrumentedTestConstants.SECRET_KEY
            );

            instrumentationTestFacade.getApiWrapper().setAuthorization(account, keyPair);
        }

        startActivity(testNameRule.getMethodName());
    }

    public void teardown() throws IOException {
        //if test fail, unregister all resources
        for (IdlingResource resource : registeredResources) {
            IdlingRegistry.getInstance().unregister(resource);
        }

        activityRule.finishActivity();
    }

    @FunctionalInterface
    public interface Exceptionable {
        void run() throws Exception;
    }
}
