package im.adamant.android.dagger;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Screens;
import im.adamant.android.ui.presenters.ShowQrCodePresenter;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class ShowQrCodeScreenModule {
    @ActivityScope
    @Provides
    public static ShowQrCodePresenter providePresenter(
            @Named(Screens.SHOW_QRCODE_SCREEN) CompositeDisposable subscriptions
    ) {
        return new ShowQrCodePresenter(subscriptions);
    }

    @ActivityScope
    @Provides
    @Named(value = Screens.SHOW_QRCODE_SCREEN)
    public CompositeDisposable provideComposite() {
        return new CompositeDisposable();
    }
}
