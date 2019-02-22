package im.adamant.android.dagger.fragments;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Screens;
import im.adamant.android.helpers.QrCodeHelper;

import static org.mockito.Mockito.mock;

@Module
public class TestCreateChatScreenModule {
    @FragmentScope
    @Provides
    @Named(value = Screens.CREATE_CHAT_SCREEN)
    public QrCodeHelper provideQrCodeParser() {
        return mock(QrCodeHelper.class);
    }
}
