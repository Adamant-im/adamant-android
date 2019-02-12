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
import im.adamant.android.interactors.SubscribeToPushInteractor;
import im.adamant.android.shadows.FirebaseInstanceIdShadow;
import im.adamant.android.shadows.LocaleChangerShadow;
import im.adamant.android.ui.mvp_view.SettingsView;
import im.adamant.android.ui.presenters.SettingsPresenter;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

import static im.adamant.android.ui.mvp_view.SettingsView.IS_RECEIVE_NOTIFICATIONS;
import static im.adamant.android.ui.mvp_view.SettingsView.IS_SAVE_KEYPAIR;
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
    SubscribeToPushInteractor subscribeInteractor;

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
                subscribeInteractor,
                disposable
        );
        when(saveKeypairInteractor.getFlowable()).thenReturn(Flowable.empty());
    }

    @After
    public void tearDown() {
        if (disposable != null) {
            disposable.dispose();
        }
    }

    @Test
    public void testSuccessSaveAllSettings() {
        when(subscribeInteractor.getEventsObservable()).thenReturn(Flowable.just(SubscribeToPushInteractor.Event.SUBSCRIBED));

        Bundle bundle = new Bundle();
        bundle.putBoolean(IS_SAVE_KEYPAIR, true);
        bundle.putBoolean(IS_RECEIVE_NOTIFICATIONS, true);

        presenter.attachView(view);
        presenter.onClickSaveSettings(bundle);

        verify(subscribeInteractor).savePushToken(TestConstants.FAKE_FCM_TOKEN);
        verify(subscribeInteractor).getEventsObservable();
        verify(saveKeypairInteractor).getFlowable();
        verify(saveKeypairInteractor).saveKeypair(true);
    }

    @Test
    public void testUnsubscribePushIfKeypairNotSaved() {
        when(subscribeInteractor.getEventsObservable()).thenReturn(Flowable.just(SubscribeToPushInteractor.Event.UNSUBSCRIBED));
        Bundle bundle = new Bundle();
        bundle.putBoolean(IS_SAVE_KEYPAIR, false);
        bundle.putBoolean(IS_RECEIVE_NOTIFICATIONS, true);

        presenter.attachView(view);
        presenter.onClickSaveSettings(bundle);

        verify(subscribeInteractor).deleteCurrentToken();
        verify(subscribeInteractor).getEventsObservable();
        verify(saveKeypairInteractor).getFlowable();
        verify(saveKeypairInteractor).saveKeypair(false);
    }

    @Test
    public void testUnsubscribePush() {
        when(subscribeInteractor.getEventsObservable()).thenReturn(Flowable.just(SubscribeToPushInteractor.Event.UNSUBSCRIBED));

        Bundle bundle = new Bundle();
        bundle.putBoolean(IS_SAVE_KEYPAIR, true);
        bundle.putBoolean(IS_RECEIVE_NOTIFICATIONS, false);

        presenter.attachView(view);
        presenter.onClickSaveSettings(bundle);

        verify(subscribeInteractor).deleteCurrentToken();
        verify(subscribeInteractor).getEventsObservable();
        verify(saveKeypairInteractor).getFlowable();
        verify(saveKeypairInteractor).saveKeypair(true);
    }

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
