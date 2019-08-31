package im.adamant.android;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.fragment.app.FragmentManager;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import im.adamant.android.base.BaseTest;
import im.adamant.android.idling_resources.FragmentIdlingResource;
import im.adamant.android.ui.LoginScreen;
import im.adamant.android.ui.NodesListScreen;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class LoginUITest extends BaseTest<LoginScreen> {

    @Rule
    public final ActivityTestRule<LoginScreen> mActivityRule = new ActivityTestRule<>(LoginScreen.class, true, false);

    @Before
    public void before() {
        Intents.init();
        mActivityRule.launchActivity(null);
    }

    @After
    public void after() {
        Intents.release();
        mActivityRule.finishActivity();
    }

    @Override
    protected boolean isProtectedScreen() {
        return false;
    }

    @Override
    protected void startActivity(String testName) {
        activityRule.launchActivity(null);
    }

    @Override
    protected Class<LoginScreen> provideActivityClass() {
        return LoginScreen.class;
    }

    @Test
    @LargeTest
    public void uiShowLoginFragment() throws Exception {
        LoginScreen activity = mActivityRule.getActivity();

        onView(withId(R.id.activity_login_btn_login)).perform(click());

        FragmentManager supportFragmentManager = activity.getSupportFragmentManager();
        FragmentIdlingResource dialogFragmentIdlingResource = new FragmentIdlingResource(
                LoginScreen.BOTTOM_LOGIN_TAG,
                supportFragmentManager
        );

        idlingBlock(dialogFragmentIdlingResource, () -> {
            onView(withId(R.id.fragment_login_et_passphrase))
                    .check(matches(isDisplayed()));
        });
    }

    @Test
    @LargeTest
    public void uiShowRegistrationScreen() {
        onView(withId(R.id.activity_login_ib_node_list)).check(matches(isDisplayed()));
        onView(withId(R.id.activity_login_ib_node_list)).perform(click());
        intended(hasComponent(NodesListScreen.class.getName()));
    }
}
