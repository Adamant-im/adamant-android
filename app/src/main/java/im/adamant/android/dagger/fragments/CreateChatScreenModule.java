package im.adamant.android.dagger.fragments;

import java.util.Map;

import im.adamant.android.Screens;
import im.adamant.android.markdown.AdamantAddressExtractor;
import im.adamant.android.helpers.QrCodeHelper;
import im.adamant.android.interactors.ChatUpdatePublicKeyInteractor;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
import im.adamant.android.interactors.wallets.WalletFacade;
import im.adamant.android.ui.presenters.CreateChatPresenter;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.interactors.chats.ChatsStorage;
import ru.terrakok.cicerone.Router;

@Module
public class CreateChatScreenModule {
    @FragmentScope
    @Provides
    @Named(value = Screens.CREATE_CHAT_SCREEN)
    public QrCodeHelper provideQrCodeParser() {
        return new QrCodeHelper();
    }

    @FragmentScope
    @Provides
    public static CreateChatPresenter provideCreateChatPresenter(
            Router router,
            Map<SupportedWalletFacadeType, WalletFacade> wallets,
            AdamantAddressExtractor adamantAddressExtractor,
            ChatUpdatePublicKeyInteractor chatUpdatePublicKeyInteraactor,
            ChatsStorage chatsStorage
    ){
        return new CreateChatPresenter(
                router,
                wallets,
                chatUpdatePublicKeyInteraactor,
                adamantAddressExtractor,
                chatsStorage
        );
    }
}
