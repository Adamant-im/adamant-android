package im.adamant.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;

import im.adamant.android.base.ActivityWithMockingNetworkUITest;
import im.adamant.android.dispatchers.ServerDownOnSendMessageDispatcher;
import im.adamant.android.dispatchers.SuccessAuthorizationDispatcher;
import im.adamant.android.helpers.Settings;
import im.adamant.android.idling_resources.ActivityIdlingResosurce;
import im.adamant.android.interactors.SecurityInteractor;
import im.adamant.android.interactors.push.SupportedPushNotificationFacadeType;
import im.adamant.android.ui.LoginScreen;
import im.adamant.android.ui.MainScreen;
import im.adamant.android.ui.PincodeScreen;
import im.adamant.android.ui.mvp_view.PinCodeView;
import im.adamant.android.utils.InstrumentedTestConstants;
import im.adamant.android.utils.ToastMatcher;
import okhttp3.mockwebserver.Dispatcher;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static im.adamant.android.utils.TestUtils.withRecyclerView;
import static org.hamcrest.Matchers.allOf;

public class PincodeInAccessModeUINetworkTest extends ActivityWithMockingNetworkUITest<PincodeScreen> {
    @Override
    protected Dispatcher provideDispatcher(String testName) {
        switch (testName) {
            case "uiNetDropPinWithFailFCMPushUnsubscribe": {
                return new ServerDownOnSendMessageDispatcher(application);
            }
            case "uiNetSuccessAccessByPincode": {
                return new SuccessAuthorizationDispatcher(application);
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
            case "uiNetDropPinWithFailFCMPushUnsubscribe": {
                InstrumentationTestFacade testFacade = application.getInstrumentationTestFacade();
                SecurityInteractor securityInteractor = testFacade.getSecurityInteractor();
                securityInteractor
                        .savePassphrase(InstrumentedTestConstants.PINCODE)
                        .blockingAwait();

                Settings settings = testFacade.getSettings();
                settings.setPushNotificationFacadeType(SupportedPushNotificationFacadeType.FCM);
                settings.setUnsubscribeFcmTransaction("{ \n" +
                        "   \"amount\":0,\n" +
                        "   \"asset\":{ \n" +
                        "      \"chat\":{ \n" +
                        "         \"message\":\"3c1a3dbad9ca7b76e5e2e3e1b2a8b94de4ce2b16d950c9d0fd6cdf87c8891c727d0bd9f5ac28f5ef2230cde51cb1e8e8e381a74ce8483a97669e7962909fe8c780a9fb28e8a68454c333070c17d04b3dde2016c3c1fe981ca475ccd91489deeecaf983b68db5f88e49de873fbfdaf5527de191707a8d424c39432c9633eb4fb5c647133b26eaa55d2ea46c44ed3a18ff7ab42484dfd242ce096ffd2212eee1c5e3be84398c0dbd1649b631e56d71d7926618d5571965f7d5fe2527c94eb57192f79e83915ead37a227deeaad1c4bd2746455223d8173e1aafb11dd418c28da6d8fa63696\",\n" +
                        "         \"own_message\":\"a9e4a02e9e5f13e7ea788fe8619eb4600701f257af4a11fd\",\n" +
                        "         \"type\":3\n" +
                        "      }\n" +
                        "   },\n" +
                        "   \"confirmations\":0,\n" +
                        "   \"fee\":0,\n" +
                        "   \"height\":0,\n" +
                        "   \"recipientId\":\"U15243615587463307445\",\n" +
                        "   \"senderId\":\"U1119781441708645832\",\n" +
                        "   \"senderPublicKey\":\"716a3fe588d5b7130c8c3a553e4f421f01f7e72506e79638406008091ca32c20\",\n" +
                        "   \"signature\":\"e194189f38b4bd6cc83da077504e755dcdc3b477b85c3e8b0a4f5d46798a5dbc0819a0bacdd51be4d14731bb32d5a1490c14af7470f414735f103f68c7279106\",\n" +
                        "   \"timestamp\":65022934,\n" +
                        "   \"type\":8\n" +
                        "}");

                activityRule.launchActivity(provideIntent(PinCodeView.MODE.ACCESS_TO_APP));
            }
            break;
            case "uiNetWrongPincode":
            case "uiNetSuccessAccessByPincode":
            case "uiNetFailAccessByPincodeServerNotResponse":
            case "uiDropPinDisabledPushUnsubscribe": {
                InstrumentationTestFacade testFacade = application.getInstrumentationTestFacade();
                testFacade.getApiWrapper().setPassPhrase(InstrumentedTestConstants.PASSPHRASE);
                SecurityInteractor securityInteractor = testFacade.getSecurityInteractor();
                securityInteractor
                        .savePassphrase(InstrumentedTestConstants.PINCODE)
                        .blockingAwait();

                Settings settings = testFacade.getSettings();
                settings.setPushNotificationFacadeType(SupportedPushNotificationFacadeType.DISABLED);

                activityRule.launchActivity(provideIntent(PinCodeView.MODE.ACCESS_TO_APP));
            }
            break;
        }
    }

    @Override
    protected Class<PincodeScreen> provideActivityClass() {
        return PincodeScreen.class;
    }

    @Test
    public void uiNetDropPinWithFailFCMPushUnsubscribe() {
        onView(withId(R.id.activity_pin_code_btn_reset_or_cancel)).perform(scrollTo(), click());

        onView(withText(R.string.unsubscribe_push_error)).inRoot(new ToastMatcher())
                    .check(matches(isDisplayed()));
    }

    @Test
    public void uiDropPinDisabledPushUnsubscribe() throws Exception {
        onView(withId(R.id.activity_pin_code_btn_reset_or_cancel)).perform(scrollTo(), click());

        String loginActivity = LoginScreen.class.getName();
        ActivityIdlingResosurce activityIdlingResosurce = new ActivityIdlingResosurce(loginActivity);

        idlingBlock(activityIdlingResosurce, () -> {
            intended(hasComponent(LoginScreen.class.getName()));
        });
    }

    @Test
    public void uiNetSuccessAccessByPincode() throws Exception {
        typePincode(R.id.activity_pincode_plv_keyboard, InstrumentedTestConstants.PINCODE);

        String mainActivity = MainScreen.class.getName();
        ActivityIdlingResosurce activityIdlingResosurce = new ActivityIdlingResosurce(mainActivity);

        idlingBlock(activityIdlingResosurce, () -> {
            intended(hasComponent(MainScreen.class.getName()));
        });
    }

    @Test
    public void uiNetFailAccessByPincodeServerNotResponse() throws Exception {
        typePincode(R.id.activity_pincode_plv_keyboard, InstrumentedTestConstants.PINCODE);

        onView(withText(R.string.pincode_authorization_error)).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    @Test
    public void uiNetWrongPincode() {
        typePincode(R.id.activity_pincode_plv_keyboard, "445544");

        String errText = application.getString(R.string.wrong_pincode, BuildConfig.MAX_WRONG_PINCODE_ATTEMTS - 1);

        onView(withText(errText)).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    private Intent provideIntent(PinCodeView.MODE mode) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PinCodeView.ARG_MODE, mode);

        Intent intent = new Intent(application.getApplicationContext(), PincodeScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtras(bundle);

        return intent;
    }

    private void typePincode(int recyclerId, String pincode) {
        for (int i = 0; i < pincode.length(); i++) {
            onView(withId(recyclerId))
                    .perform(actionOnItem(withChild(withText(pincode.substring(i, i + 1))), click()));
        }
    }
}
