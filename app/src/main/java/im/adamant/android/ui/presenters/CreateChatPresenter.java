package im.adamant.android.ui.presenters;

import com.arellomobile.mvp.InjectViewState;

import java.util.List;
import java.util.Map;

import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.helpers.AdamantAddressProcessor;
import im.adamant.android.helpers.ChatsStorage;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.ChatUpdatePublicKeyInteractor;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
import im.adamant.android.interactors.wallets.WalletFacade;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.mvp_view.CreateChatView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class CreateChatPresenter extends BasePresenter<CreateChatView>{
    private Router router;
    private Map<SupportedWalletFacadeType, WalletFacade> wallets;
    private ChatsStorage chatsStorage;
    private ChatUpdatePublicKeyInteractor chatUpdatePublicKeyInteraactor;
    private AdamantAddressProcessor addressProcessor;

    public CreateChatPresenter(
            Router router,
            Map<SupportedWalletFacadeType, WalletFacade> wallets,
            ChatUpdatePublicKeyInteractor chatUpdatePublicKeyInteraactor,
            AdamantAddressProcessor addressProcessor,
            ChatsStorage chatsStorage,
            CompositeDisposable subscriptions
    ) {
        super(subscriptions);
        this.router = router;
        this.chatsStorage = chatsStorage;
        this.chatUpdatePublicKeyInteraactor = chatUpdatePublicKeyInteraactor;
        this.addressProcessor = addressProcessor;
        this.wallets = wallets;
    }

    public void onInputAddress(String addressPart) {
        List<AdamantAddressProcessor.AdamantAddressEntity> addresses = addressProcessor.extractAdamantAddresses(addressPart);

        if (addresses.size() == 0){
            getViewState().showError(R.string.wrong_address);
            getViewState().lockUI();
            return;
        }

        AdamantAddressProcessor.AdamantAddressEntity addressEntity = addresses.get(0);

        if (!validate(addressEntity.getAddress())){
            getViewState().showError(R.string.wrong_address);
            getViewState().lockUI();
        } else {
            getViewState().unlockUI();
        }
    }

    public void onClickCreateNewChat(String addressUriString) {
        List<AdamantAddressProcessor.AdamantAddressEntity> addresses = addressProcessor.extractAdamantAddresses(addressUriString);

        if (addresses.size() == 0){
            getViewState().showError(R.string.wrong_address);
            return;
        }

        AdamantAddressProcessor.AdamantAddressEntity addressEntity = addresses.get(0);

        if (validate(addressEntity.getAddress())){
            Chat chat = new Chat();
            chat.setCompanionId(addressEntity.getAddress());
            chat.setTitle(addressEntity.getLabel());

            Disposable subscribe = chatUpdatePublicKeyInteraactor
                    .execute(chat)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            chatWithKey -> {
                                chatsStorage.addNewChat(chatWithKey);
                                router.navigateTo(Screens.MESSAGES_SCREEN, addressEntity.getAddress());
                            },
                            error -> LoggerHelper.e("createChatPresenter", error.getMessage())
                    );
            subscriptions.add(subscribe);
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
