package im.adamant.android.presenters;

import com.arellomobile.mvp.InjectViewState;

import java.util.List;

import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.helpers.AdamantAddressProcessor;
import im.adamant.android.interactors.SendMessageInteractor;
import im.adamant.android.helpers.ChatsStorage;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.mvp_view.CreateChatView;

import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class CreateChatPresenter extends BasePresenter<CreateChatView>{
    private Router router;
    private SendMessageInteractor interactor;
    private ChatsStorage chatsStorage;
    private AdamantAddressProcessor addressProcessor;

    public CreateChatPresenter(
            Router router,
            SendMessageInteractor interactor,
            AdamantAddressProcessor addressProcessor,
            ChatsStorage chatsStorage,
            CompositeDisposable subscriptions
    ) {
        super(subscriptions);
        this.router = router;
        this.interactor = interactor;
        this.chatsStorage = chatsStorage;
        this.addressProcessor = addressProcessor;
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

            chatsStorage.addNewChat(chat);
            router.navigateTo(Screens.MESSAGES_SCREEN, chat);

        } else {
           getViewState().showError(R.string.wrong_address);
        }
    }

    public void onClickScanQrCodeButton() {
        router.navigateTo(Screens.SCAN_QRCODE_SCREEN);
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
