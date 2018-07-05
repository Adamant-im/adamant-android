package im.adamant.android.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.List;

import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.helpers.AdamantAddressProcessor;
import im.adamant.android.interactors.ChatsInteractor;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.mvp_view.CreateChatView;

import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class CreateChatPresenter extends BasePresenter<CreateChatView>{
    private Router router;
    private ChatsInteractor interactor;
    private AdamantAddressProcessor addressProcessor;

    public CreateChatPresenter(Router router, ChatsInteractor interactor, AdamantAddressProcessor addressProcessor, CompositeDisposable subscriptions) {
        super(subscriptions);
        this.router = router;
        this.interactor = interactor;
        this.addressProcessor = addressProcessor;
    }

    public void onClickCreateNewChat(String address) {
        List<String> addresses = addressProcessor.extractAdamantAddreses(address);

        if (addresses.size() == 0){
            getViewState().showError(R.string.wrong_address);
            return;
        }

        address = addresses.get(0);

        if (validate(address)){
            Chat chat = new Chat();
            chat.setCompanionId(address);

            interactor.addNewChat(chat);
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
