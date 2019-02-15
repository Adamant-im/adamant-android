package im.adamant.android.cases.presenters;

import android.content.Context;
import android.os.Bundle;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import im.adamant.android.TestApplication;
import im.adamant.android.TestConstants;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.entities.Account;
import im.adamant.android.dagger.DaggerTestAppComponent;
import im.adamant.android.dagger.TestAppComponent;
import im.adamant.android.interactors.SaveKeypairInteractor;
import im.adamant.android.interactors.SwitchPushNotificationServiceInteractor;
import im.adamant.android.shadows.FirebaseInstanceIdShadow;
import im.adamant.android.shadows.LocaleChangerShadow;
import im.adamant.android.ui.mvp_view.SettingsView;
import im.adamant.android.ui.presenters.SettingsPresenter;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(
        sdk = Config.TARGET_SDK,
        manifest = Config.NONE,
        shadows = {
                LocaleChangerShadow.class,
                FirebaseInstanceIdShadow.class
        },
        application = TestApplication.class
)
public class SettingsPresenterTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Inject
    SwitchPushNotificationServiceInteractor switchPushNotificationServiceInteractor;

    @Inject
    SaveKeypairInteractor saveKeypairInteractor;

    @Inject
    Router router;

    @Inject
    AdamantApiWrapper api;

    SettingsView view;

    CompositeDisposable disposable;

    SettingsPresenter presenter;

    @Before
    public void setUp() {
        Context systemContext = RuntimeEnvironment.systemContext;

        TestAppComponent component = DaggerTestAppComponent
                .builder()
                .context(systemContext)
                .build();

        component.inject(this);

        view = mock(SettingsView.class);
        disposable = new CompositeDisposable();

        presenter = new SettingsPresenter(
                router,
                api,
                saveKeypairInteractor,
                switchPushNotificationServiceInteractor,
                disposable
        );

        when(switchPushNotificationServiceInteractor.changeNotificationFacade(any())).thenReturn(Completable.complete());
        when(saveKeypairInteractor.getFlowable()).thenReturn(Flowable.empty());
    }

    @After
    public void tearDown() {
        if (disposable != null) {
            disposable.dispose();
        }
    }

    //TODO: Need to check the key save

//    @Test
//    public void testSuccessSaveAllSettings() {
//        presenter.attachView(view);
//        presenter.onClickSaveSettings(bundle);
//
//        verify(subscribeInteractor).enablePush(true);
//        verify(subscribeInteractor).savePushToken(TestConstants.FAKE_FCM_TOKEN);
//        verify(subscribeInteractor).getEventsObservable();
//        verify(saveKeypairInteractor).getFlowable();
//        verify(saveKeypairInteractor).saveKeypair(true);
//    }
//
//    @Test
//    public void testUnsubscribePushIfKeypairNotSaved() {
//        Bundle bundle = new Bundle();
//        bundle.putBoolean(IS_SAVE_KEYPAIR, false);
//        bundle.putBoolean(IS_RECEIVE_NOTIFICATIONS, true);
//
//        presenter.attachView(view);
//        presenter.onClickSaveSettings(bundle);
//
//        verify(subscribeInteractor).enablePush(false);
//        verify(subscribeInteractor).deleteCurrentToken();
//        verify(subscribeInteractor).getEventsObservable();
//        verify(saveKeypairInteractor).getFlowable();
//        verify(saveKeypairInteractor).saveKeypair(false);
//    }
//
//    @Test
//    public void testUnsubscribePush() {
//        Bundle bundle = new Bundle();
//        bundle.putBoolean(IS_SAVE_KEYPAIR, true);
//        bundle.putBoolean(IS_RECEIVE_NOTIFICATIONS, false);
//
//        presenter.attachView(view);
//        presenter.onClickSaveSettings(bundle);
//
//        verify(subscribeInteractor).enablePush(false);
//        verify(subscribeInteractor).deleteCurrentToken();
//        verify(subscribeInteractor).getEventsObservable();
//        verify(saveKeypairInteractor).getFlowable();
//        verify(saveKeypairInteractor).saveKeypair(true);
//    }

    @Test
    public void testIfZeroBalanceSubscriptionOnNotificationsUnavailable() {
        Account account = new Account();
        account.setBalance(0);

        when(saveKeypairInteractor.isKeyPairMustBeStored()).thenReturn(true);
        when(api.isAuthorized()).thenReturn(true);
        when(api.getAccount()).thenReturn(account);

        presenter.attachView(view);

        verify(view).setEnablePushOption(false);
    }
}
