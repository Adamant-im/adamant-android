package im.adamant.android.cases.presenters;

import android.content.Context;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import im.adamant.android.Screens;
import im.adamant.android.TestApplication;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.entities.Account;
import im.adamant.android.dagger.DaggerTestAppComponent;
import im.adamant.android.dagger.TestAppComponent;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.LogoutInteractor;
import im.adamant.android.interactors.SecurityInteractor;
import im.adamant.android.interactors.SwitchPushNotificationServiceInteractor;
import im.adamant.android.shadows.FirebaseInstanceIdShadow;
import im.adamant.android.shadows.LocaleChangerShadow;
import im.adamant.android.ui.mvp_view.SettingsView;
import im.adamant.android.ui.presenters.SettingsPresenter;
import io.reactivex.Completable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ru.terrakok.cicerone.Router;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
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
    SecurityInteractor securityInteractor;

    @Inject
    AccountInteractor accountInteractor;

    @Inject
    LogoutInteractor logoutInteractor;

    @Inject
    Router router;

    @Inject
    AdamantApiWrapper api;

    SettingsView view;

    CompositeDisposable disposable;

    SettingsPresenter presenter;

    //TODO: No need Robolectric now

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
                accountInteractor,
                logoutInteractor,
                api,
                securityInteractor,
                switchPushNotificationServiceInteractor,
                Schedulers.trampoline()
        );
    }

    @After
    public void tearDown() {
        if (disposable != null) {
            disposable.dispose();
        }
    }

    //TODO: Проверить включение отключение опции сохранение ключей
    //TODO: Проверить Положительный и нулевой баланс

    @Test
    public void testAttachView() {
        presenter.attachView(view);

        //TODO: Уточни кокретные значения
        verify(view).displayCurrentNotificationFacade(any());
        verify(view).setEnablePushOption(anyBoolean());
        verify(view).setCheckedStoreKeyPairOption(anyBoolean());

    }

    @Test
    public void testOnSetCheckedStoreKeypairInTrueState() {
        when(securityInteractor.isKeyPairMustBeStored()).thenReturn(false);
        when(switchPushNotificationServiceInteractor.resetNotificationFacade(false)).thenReturn(Completable.complete());

        presenter.attachView(view);
        presenter.onSetCheckedStoreKeypair(true, true);

        ArgumentCaptor<Boolean> enablePushArgumentCaptor = ArgumentCaptor.forClass(Boolean.class);

        verify(view, times(1)).setEnablePushOption(enablePushArgumentCaptor.capture());

        Assert.assertEquals(false, enablePushArgumentCaptor.getAllValues().get(0));

        verify(router).navigateTo(eq(Screens.PINCODE_SCREEN), any());
    }



    @Test
    public void testIfZeroBalanceSubscriptionOnNotificationsUnavailable() {
        Account account = new Account();
        account.setBalance(0);

        when(securityInteractor.isKeyPairMustBeStored()).thenReturn(true);
        when(api.isAuthorized()).thenReturn(true);
        when(api.getAccount()).thenReturn(account);

        presenter.attachView(view);

        verify(view).setEnablePushOption(false);
    }
}
