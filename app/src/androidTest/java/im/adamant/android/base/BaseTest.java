package im.adamant.android.base;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.platform.app.InstrumentationRegistry;

import com.goterl.lazycode.lazysodium.utils.KeyPair;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import im.adamant.android.AdamantApplication;
import im.adamant.android.InstrumentationTestFacade;
import im.adamant.android.core.entities.Account;
import im.adamant.android.utils.InstrumentedTestConstants;

public abstract class BaseTest {
    private Set<IdlingResource> registeredResources = new HashSet<>();

    protected abstract boolean isProtectedScreen();

    protected void idlingBlock(IdlingResource idlingResource, Runnable runnable) {
        IdlingRegistry.getInstance().register(idlingResource);
        registeredResources.add(idlingResource);
        runnable.run();
        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    public void setup() throws IOException {
        if (isProtectedScreen()) {
            AdamantApplication application = (AdamantApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
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
    }

    public void teardown() throws IOException {
        //if test fail, unregister all resources
        for (IdlingResource resource : registeredResources) {
            IdlingRegistry.getInstance().unregister(resource);
        }
    }
}
