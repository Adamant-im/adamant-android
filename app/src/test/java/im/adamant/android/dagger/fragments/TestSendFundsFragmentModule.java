package im.adamant.android.dagger.fragments;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Screens;
import im.adamant.android.ui.presenters.SendFundsPresenter;
import io.reactivex.disposables.CompositeDisposable;

import static org.mockito.Mockito.mock;

@Module
public class TestSendFundsFragmentModule {
    @FragmentScope
    @Provides
    public static SendFundsPresenter provideSendFundsPresenter() {
        return mock(SendFundsPresenter.class);
    }

    @FragmentScope
    @Provides
    @Named(value = Screens.SEND_CURRENCY_TRANSFER_SCREEN)
    public static CompositeDisposable provideSendCurrencyTransferComposite() {
        return new CompositeDisposable();
    }
}
