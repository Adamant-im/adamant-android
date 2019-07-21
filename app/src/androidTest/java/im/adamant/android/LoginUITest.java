package im.adamant.android;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import im.adamant.android.idling_resources.DialogFragmentIdlingResource;
import im.adamant.android.ui.LoginScreen;
import im.adamant.android.ui.MainScreen;
import im.adamant.android.ui.NodesListScreen;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class LoginUITest {

    @Rule
    public final ActivityTestRule<LoginScreen> mActivityRule = new ActivityTestRule<>(LoginScreen.class);

    @Before
    public void before() {
        Intents.init();
    }

    @After
    public void after() {
        Intents.release();
    }

    @Test
    public void uiShowLoginFragment() {
        LoginScreen activity = mActivityRule.getActivity();
        FragmentManager supportFragmentManager = activity.getSupportFragmentManager();

        DialogFragmentIdlingResource dialogFragmentIdlingResource = new DialogFragmentIdlingResource(
                LoginScreen.BOTTOM_LOGIN_TAG,
                supportFragmentManager
        );

        onView(withId(R.id.activity_login_btn_login)).perform(click());

        IdlingRegistry.getInstance().register(dialogFragmentIdlingResource);

        onView(withId(R.id.fragment_login_et_passphrase))
                .check(matches(isDisplayed()));

        IdlingRegistry.getInstance().unregister(dialogFragmentIdlingResource);
    }

    @Test
    public void uiShowRegistrationScreen() {
        onView(withId(R.id.activity_login_ib_node_list)).check(matches(isDisplayed()));
        onView(withId(R.id.activity_login_ib_node_list)).perform(click());
        intended(hasComponent(NodesListScreen.class.getName()));
    }

    
}
