package im.adamant.android.dagger.activities;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Screens;
import im.adamant.android.helpers.QrCodeHelper;
import im.adamant.android.ui.presenters.ShowQrCodePresenter;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class ShowQrCodeScreenModule {
    @ActivityScope
    @Provides
    public static ShowQrCodePresenter providePresenter(
            QrCodeHelper qrCodeHelper,
            @Named(Screens.SHOW_QRCODE_SCREEN) CompositeDisposable subscriptions
    ) {
        return new ShowQrCodePresenter(qrCodeHelper, subscriptions);
    }

    @ActivityScope
    @Provides
    @Named(value = Screens.SHOW_QRCODE_SCREEN)
    public CompositeDisposable provideComposite() {
        return new CompositeDisposable();
    }
}
