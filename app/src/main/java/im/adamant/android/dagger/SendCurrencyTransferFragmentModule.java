package im.adamant.android.dagger;

import java.util.Map;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Screens;
import im.adamant.android.helpers.ChatsStorage;
import im.adamant.android.helpers.PublicKeyStorage;
import im.adamant.android.interactors.SendCurrencyInteractor;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
import im.adamant.android.interactors.wallets.WalletFacade;
import im.adamant.android.ui.messages_support.factories.MessageFactoryProvider;
import im.adamant.android.ui.presenters.SendCurrencyPresenter;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

@Module
public class SendCurrencyTransferFragmentModule {
    @FragmentScope
    @Provides
    public SendCurrencyPresenter provideSendCurrencyPresenter(
            Router router,
            Map<SupportedWalletFacadeType, WalletFacade> wallets,
            SendCurrencyInteractor sendCurrencyInteractor,
            MessageFactoryProvider messageFactoryProvider,
            PublicKeyStorage publicKeyStorage,
            ChatsStorage chatsStorage,
            @Named(Screens.SEND_CURRENCY_TRANSFER_SCREEN) CompositeDisposable subscriptions
    ){
        return new SendCurrencyPresenter(
                router,
                wallets,
                sendCurrencyInteractor,
                messageFactoryProvider,
                publicKeyStorage,
                chatsStorage,
                subscriptions
        );
    }

    @FragmentScope
    @Provides
    @Named(value = Screens.SEND_CURRENCY_TRANSFER_SCREEN)
    public CompositeDisposable provideComposite() {
        return new CompositeDisposable();
    }
}
