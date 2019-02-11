package im.adamant.android.cases.services;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ServiceController;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import im.adamant.android.TestApplication;
import im.adamant.android.dagger.DaggerTestAppComponent;
import im.adamant.android.dagger.TestAppComponent;
import im.adamant.android.dagger.TestInteractorsModule;
import im.adamant.android.helpers.Settings;
import im.adamant.android.interactors.SubscribeToPushInteractor;
import im.adamant.android.services.SaveSettingsService;
import im.adamant.android.shadows.LocaleChangerShadow;
import sm.euzee.github.com.servicemanager.ServiceManager;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(
        sdk = Config.TARGET_SDK,
        manifest = Config.NONE,
        shadows = {LocaleChangerShadow.class},
        application = TestApplication.class
)
public class SaveSettingsServiceTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private SaveSettingsService service;
    private ServiceController<SaveSettingsService> controller;

    @Inject
    SubscribeToPushInteractor mockSubscribeInteractor;

    @Test
    public void lifecycleTest() {
        Context systemContext = RuntimeEnvironment.systemContext;

        TestAppComponent component = DaggerTestAppComponent
                .builder()
                .context(systemContext)
                .build();

        component.inject(this);

        Intent intent = new Intent(systemContext, SaveSettingsService.class);
        intent.putExtra(SaveSettingsService.IS_SAVE_KEYPAIR, true);
        intent.putExtra(SaveSettingsService.IS_RECEIVE_NOTIFICATIONS, true);

        controller = Robolectric.buildService(SaveSettingsService.class, intent);
        service = controller.bind().create().get();

//        verify(mockSubscribeInteractor).savePushToken(any());
        verify(mockSubscribeInteractor).enablePush(anyBoolean());

        controller.destroy();

    }
}
