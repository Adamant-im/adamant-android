package im.adamant.android.ui.presenters;

import com.arellomobile.mvp.InjectViewState;

import java.util.List;
import java.util.Map;

import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.markdown.AdamantAddressEntity;
import im.adamant.android.markdown.AdamantAddressExtractor;
import im.adamant.android.interactors.chats.ChatsStorage;
import im.adamant.android.interactors.ChatUpdatePublicKeyInteractor;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
import im.adamant.android.interactors.wallets.WalletFacade;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.mvp_view.CreateChatView;

import ru.terrakok.cicerone.Router;

@InjectViewState
public class CreateChatPresenter extends BasePresenter<CreateChatView>{
    private Router router;
    private Map<SupportedWalletFacadeType, WalletFacade> wallets;
    private ChatsStorage chatsStorage;
    private ChatUpdatePublicKeyInteractor chatUpdatePublicKeyInteractor;
    private AdamantAddressExtractor adamantAddressExtractor;

    public CreateChatPresenter(
            Router router,
            Map<SupportedWalletFacadeType, WalletFacade> wallets,
            ChatUpdatePublicKeyInteractor chatUpdatePublicKeyInteractor,
            AdamantAddressExtractor adamantAddressExtractor,
            ChatsStorage chatsStorage
    ) {
        this.router = router;
        this.chatsStorage = chatsStorage;
        this.chatUpdatePublicKeyInteractor = chatUpdatePublicKeyInteractor;
        this.adamantAddressExtractor = adamantAddressExtractor;
        this.wallets = wallets;
    }

    public void onInputAddress(String addressPart) {
        List<AdamantAddressEntity> addresses = adamantAddressExtractor.extractAdamantAddresses(addressPart);

        if (addresses.size() == 0){
            getViewState().showError(R.string.wrong_address);
            getViewState().lockUI();
            return;
        }

        AdamantAddressEntity addressEntity = addresses.get(0);

        if (!validate(addressEntity.getAddress())){
            getViewState().showError(R.string.wrong_address);
            getViewState().lockUI();
        } else {
            getViewState().unlockUI();
        }
    }

    public void onClickCreateNewChat(String addressUriString) {
        List<AdamantAddressEntity> addresses = adamantAddressExtractor.extractAdamantAddresses(addressUriString);

        if (addresses.size() == 0){
            getViewState().showError(R.string.wrong_address);
            return;
        }

        AdamantAddressEntity addressEntity = addresses.get(0);

        if (validate(addressEntity.getAddress())){
            Chat chat = new Chat();
            chat.setCompanionId(addressEntity.getAddress());
            chat.setTitle(addressEntity.getLabel());
            chatUpdatePublicKeyInteractor.execute(chat);
            chatsStorage.addNewChat(chat);
            router.navigateTo(Screens.MESSAGES_SCREEN, addressEntity.getAddress());

        } else {
           getViewState().showError(R.string.wrong_address);
        }
    }

    public void onClickScanQrCodeButton() {
        router.navigateTo(Screens.SCAN_QRCODE_SCREEN);
    }

    public void onClickShowMyQrCodeButton() {
        WalletFacade facade = wallets.get(SupportedWalletFacadeType.ADM);
        if (facade != null) {
            String address = facade.getAddress();
            getViewState().showQrCode(address);
        }
    }

    private boolean validate(String address) {
        if (address == null) {return false;}
        try {
            if (!"U".equalsIgnoreCase(address.substring(0, 1))){return false;}
            if (address.length() < 16){return false;}
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }

        return true;
    }
}
