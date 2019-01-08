package im.adamant.android.dagger;

import java.util.Map;

import im.adamant.android.Screens;
import im.adamant.android.helpers.AdamantAddressProcessor;
import im.adamant.android.helpers.QrCodeHelper;
import im.adamant.android.interactors.wallets.AdamantWalletFacade;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
import im.adamant.android.interactors.wallets.WalletFacade;
import im.adamant.android.ui.presenters.CreateChatPresenter;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.helpers.ChatsStorage;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

@Module
public class CreateChatScreenModule {
    @ActivityScope
    @Provides
    public CreateChatPresenter provideLoginPresenter(
            Router router,
            Map<SupportedWalletFacadeType, WalletFacade> wallets,
            AdamantAddressProcessor addressProcessor,
            ChatsStorage chatsStorage,
            @Named(Screens.CREATE_CHAT_SCREEN) CompositeDisposable subscriptions
    ){
        return new CreateChatPresenter(router, wallets, addressProcessor, chatsStorage, subscriptions);
    }

    @ActivityScope
    @Provides
    @Named(value = Screens.CREATE_CHAT_SCREEN)
    public CompositeDisposable provideComposite() {
        return new CompositeDisposable();
    }

    @ActivityScope
    @Provides
    @Named(value = Screens.CREATE_CHAT_SCREEN)
    public QrCodeHelper provideQrCodeParser() {
        return new QrCodeHelper();
    }
}
