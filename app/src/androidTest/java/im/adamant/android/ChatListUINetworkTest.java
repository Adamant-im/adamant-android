package im.adamant.android;

import android.content.Intent;

import com.azimolabs.conditionwatcher.ConditionWatcher;
import org.junit.Test;

import im.adamant.android.base.ActivityWithMockingNetworkUITest;
import im.adamant.android.dispatchers.ChatListPeriodicTimeoutDispatcher;
import im.adamant.android.dispatchers.ContactsListPeriodicTimeoutDispatcher;
import im.adamant.android.idling_resources.InFragmentRecyclerViewNotEmptyWatchInstruction;
import im.adamant.android.idling_resources.FragmentIdlingResource;
import im.adamant.android.ui.MainScreen;
import im.adamant.android.ui.fragments.ChatsScreen;
import okhttp3.mockwebserver.Dispatcher;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static im.adamant.android.ui.MainScreen.ARG_CURRENT_SCREEN;
import static im.adamant.android.utils.TestUtils.withRecyclerView;

public class ChatListUINetworkTest extends ActivityWithMockingNetworkUITest<MainScreen> {
    @Override
    protected Class<MainScreen> provideActivityClass() {
        return MainScreen.class;
    }

    @Override
    protected Dispatcher provideDispatcher(String testName) {
        switch (testName) {
            case "uiNetChatlistTimeoutRestoring" : {
                return new ChatListPeriodicTimeoutDispatcher(application);
            }
            case "uiNetContactlistTimeoutRestoring" : {
                return new ContactsListPeriodicTimeoutDispatcher(application);
            }
        }
        return null;
    }

    @Override
    protected boolean isProtectedScreen() {
        return true;
    }

    @Override
    protected void startActivity(String testName) {
        Intent intent = new Intent();
        intent.putExtra(ARG_CURRENT_SCREEN, Screens.CHATS_SCREEN);
        activityRule.launchActivity(intent);
    }

    @Test
    public void uiNetChatlistTimeoutRestoring() throws Exception {
        MainScreen activity = activityRule.getActivity();
        FragmentIdlingResource chatListIdlingResource = new FragmentIdlingResource(
                ChatsScreen.class.getName(),
                activity.getSupportFragmentManager()
        );

        idlingBlock(chatListIdlingResource, () -> {
            ConditionWatcher.waitForCondition(new InFragmentRecyclerViewNotEmptyWatchInstruction(
                    activity.getSupportFragmentManager(), ChatsScreen.class.getName())
            );
            onView(withRecyclerView(R.id.fragment_chats_rv_chats)
                    .atPositionOnView(0, R.id.list_item_chat_name))
                    .check(matches(withText("U1119781441708645832")));
        });
    }

    @Test
    public void uiNetContactlistTimeoutRestoring() throws Exception {
        MainScreen activity = activityRule.getActivity();
        FragmentIdlingResource chatListIdlingResource = new FragmentIdlingResource(
                ChatsScreen.class.getName(),
                activity.getSupportFragmentManager()
        );

        idlingBlock(chatListIdlingResource, () -> {
            ConditionWatcher.waitForCondition(new InFragmentRecyclerViewNotEmptyWatchInstruction(
                    activity.getSupportFragmentManager(), ChatsScreen.class.getName())
            );
            onView(withRecyclerView(R.id.fragment_chats_rv_chats)
                    .atPositionOnView(0, R.id.list_item_chat_name))
                    .check(matches(withText("TestContactName")));
        });
    }
}
