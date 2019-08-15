package im.adamant.android;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import im.adamant.android.base.ActivityWithMockingNetworkUITest;
import im.adamant.android.dagger.AppComponent;
import im.adamant.android.dagger.DaggerAppComponent;
import im.adamant.android.ui.MainScreen;
import okhttp3.mockwebserver.Dispatcher;

public class WalletUINetworkTest extends ActivityWithMockingNetworkUITest<MainScreen> {
    @Override
    protected Class<MainScreen> provideActivityClass() {
        return MainScreen.class;
    }

    @Override
    protected Dispatcher provideDispatcher(String testName) {
        return null;
    }

    @Override
    protected boolean isProtectedScreen() {
        return true;
    }

    @Test
    public void authorizationInjectorTest() {

    }
}
