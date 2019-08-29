package im.adamant.android;

import androidx.fragment.app.FragmentManager;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import im.adamant.android.base.ActivityWithMockingNetworkUITest;
import im.adamant.android.dispatchers.SuccessAuthorizationDispatcher;
import im.adamant.android.idling_resources.ActivityIdlingResosurce;
import im.adamant.android.idling_resources.FragmentIdlingResource;
import im.adamant.android.ui.LoginScreen;
import im.adamant.android.ui.MainScreen;
import im.adamant.android.utils.InstrumentedTestConstants;
import okhttp3.mockwebserver.Dispatcher;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class LoginUINetworkTest extends ActivityWithMockingNetworkUITest<LoginScreen> {
    @Override
    protected Class<LoginScreen> provideActivityClass() {
        return LoginScreen.class;
    }

    @Override
    protected Dispatcher provideDispatcher(String testName) {
        switch (testName) {
            case "uiNetSuccessLogin": {
                return new SuccessAuthorizationDispatcher(InstrumentationRegistry.getInstrumentation().getTargetContext());
            }
        }

        return null;
    }

    @Override
    protected boolean isProtectedScreen() {
        return false;
    }

    @Override
    protected void startActivity(String testName) {
        activityRule.launchActivity(null);
    }

    @Test
    @LargeTest
    public void uiNetSuccessLogin() {

        LoginScreen activity = activityRule.getActivity();
        FragmentManager supportFragmentManager = activity.getSupportFragmentManager();

        FragmentIdlingResource dialogFragmentIdlingResource = new FragmentIdlingResource(
                LoginScreen.BOTTOM_LOGIN_TAG,
                supportFragmentManager
        );

        onView(withId(R.id.activity_login_btn_login)).perform(click());

        idlingBlock(dialogFragmentIdlingResource, () -> {
            onView(withId(R.id.fragment_login_et_passphrase))
                    .check(matches(isDisplayed()));

            onView(withId(R.id.fragment_login_et_passphrase))
                    .perform(typeText(InstrumentedTestConstants.PASSPHRASE))
                    .perform(closeSoftKeyboard());

            onView(withId(R.id.fragment_login_btn_enter)).perform(click());
        });

        String mainActivity = MainScreen.class.getName();
        ActivityIdlingResosurce activityIdlingResosurce = new ActivityIdlingResosurce(mainActivity);

        idlingBlock(activityIdlingResosurce, () -> {
            intended(hasComponent(MainScreen.class.getName()));
        });
    }

}
