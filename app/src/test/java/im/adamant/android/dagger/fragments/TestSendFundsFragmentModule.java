package im.adamant.android.dagger.fragments;

import java.util.Map;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Screens;
import im.adamant.android.helpers.ChatsStorage;
import im.adamant.android.helpers.PublicKeyStorage;
import im.adamant.android.interactors.SendFundsInteractor;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
import im.adamant.android.interactors.wallets.WalletFacade;
import im.adamant.android.ui.messages_support.factories.MessageFactoryProvider;
import im.adamant.android.ui.presenters.SendFundsPresenter;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

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
