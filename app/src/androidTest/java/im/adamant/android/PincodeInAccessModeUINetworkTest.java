package im.adamant.android;

import android.content.Intent;
import android.os.Bundle;

import org.junit.After;
import org.junit.Test;

import java.io.IOException;

import im.adamant.android.base.ActivityWithMockingNetworkUITest;
import im.adamant.android.dispatchers.ServerDownOnSendMessageDispatcher;
import im.adamant.android.idling_resources.ActivityIdlingResosurce;
import im.adamant.android.interactors.SecurityInteractor;
import im.adamant.android.ui.LoginScreen;
import im.adamant.android.ui.MainScreen;
import im.adamant.android.ui.PincodeScreen;
import im.adamant.android.ui.mvp_view.PinCodeView;
import okhttp3.mockwebserver.Dispatcher;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class PincodeInAccessModeUINetworkTest extends ActivityWithMockingNetworkUITest<PincodeScreen> {
    @Override
    protected Dispatcher provideDispatcher(String testName) {
        switch (testName) {
            case "dropPinWithFailPushUnsubscribe": {
                return new ServerDownOnSendMessageDispatcher(application);
            }
            default:
                return null;
        }
    }

    @Override
    protected boolean isProtectedScreen() {
        return true;
    }

    @Override
    protected void startActivity(String testName) {
        switch (testName) {
            case "dropPinWithFailPushUnsubscribe": {
                InstrumentationTestFacade testFacade = application.getInstrumentationTestFacade();
                SecurityInteractor securityInteractor = testFacade.getSecurityInteractor();
                securityInteractor.savePassphrase("332233");

                Bundle bundle = new Bundle();
                bundle.putSerializable(PinCodeView.ARG_MODE, PinCodeView.MODE.ACCESS_TO_APP);

                Intent intent = new Intent(application.getApplicationContext(), PincodeScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtras(bundle);

                activityRule.launchActivity(intent);
            }
        }
    }

    @Override
    protected Class<PincodeScreen> provideActivityClass() {
        return PincodeScreen.class;
    }

    @Test
    public void dropPinWithFailPushUnsubscribe() throws Exception {
        onView(withId(R.id.activity_pin_code_btn_reset_or_cancel)).perform(scrollTo(), click());

        String loginActivity = LoginScreen.class.getName();
        ActivityIdlingResosurce activityIdlingResosurce = new ActivityIdlingResosurce(loginActivity);

        idlingBlock(activityIdlingResosurce, () -> {
            intended(hasComponent(LoginScreen.class.getName()));
        });
    }
}
