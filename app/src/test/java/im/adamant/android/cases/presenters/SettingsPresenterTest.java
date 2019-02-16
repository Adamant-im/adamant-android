package im.adamant.android.cases.presenters;

import android.content.Context;
import android.os.Bundle;

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
import static org.mockito.ArgumentMatchers.anyBoolean;
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
        when(saveKeypairInteractor.isKeyPairMustBeStored()).thenReturn(false);
        when(saveKeypairInteractor.saveKeypair(true)).thenReturn(Completable.complete());
        when(switchPushNotificationServiceInteractor.resetNotificationFacade(false)).thenReturn(Completable.complete());

        presenter.attachView(view);
        presenter.onSetCheckedStoreKeypair(true);

        ArgumentCaptor<Boolean> enablePushArgumentCaptor = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<Boolean> enableStoreKeyArgumentCaptor = ArgumentCaptor.forClass(Boolean.class);

        verify(view, times(3)).setEnablePushOption(enablePushArgumentCaptor.capture());
        verify(view, times(2)).setEnableStoreKeyPairOption(enableStoreKeyArgumentCaptor.capture());

        Assert.assertEquals(false, enablePushArgumentCaptor.getAllValues().get(0));
        Assert.assertEquals(false, enablePushArgumentCaptor.getAllValues().get(1));
        Assert.assertEquals(true, enablePushArgumentCaptor.getAllValues().get(2));

        Assert.assertEquals(false, enableStoreKeyArgumentCaptor.getAllValues().get(0));
        Assert.assertEquals(true, enableStoreKeyArgumentCaptor.getAllValues().get(1));
    }

    @Test
    public void testOnSetCheckedStoreKeypairInFalseState() {
        Account account = new Account();
        account.setBalance(1_000_000_000L);

        when(saveKeypairInteractor.isKeyPairMustBeStored()).thenReturn(true);
        when(api.isAuthorized()).thenReturn(true);
        when(api.getAccount()).thenReturn(account);

        when(saveKeypairInteractor.isKeyPairMustBeStored()).thenReturn(true);
        when(saveKeypairInteractor.saveKeypair(false)).thenReturn(Completable.complete());
        when(switchPushNotificationServiceInteractor.resetNotificationFacade(true)).thenReturn(Completable.complete());

        presenter.attachView(view);
        presenter.onSetCheckedStoreKeypair(false);

        ArgumentCaptor<Boolean> enablePushArgumentCaptor = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<Boolean> enableStoreKeyArgumentCaptor = ArgumentCaptor.forClass(Boolean.class);

        verify(view, times(3)).setEnablePushOption(enablePushArgumentCaptor.capture());
        verify(view, times(2)).setEnableStoreKeyPairOption(enableStoreKeyArgumentCaptor.capture());

        Assert.assertEquals(true, enablePushArgumentCaptor.getAllValues().get(0));
        Assert.assertEquals(false, enablePushArgumentCaptor.getAllValues().get(1));
        Assert.assertEquals(false, enablePushArgumentCaptor.getAllValues().get(2));

        Assert.assertEquals(false, enableStoreKeyArgumentCaptor.getAllValues().get(0));
        Assert.assertEquals(true, enableStoreKeyArgumentCaptor.getAllValues().get(1));
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
