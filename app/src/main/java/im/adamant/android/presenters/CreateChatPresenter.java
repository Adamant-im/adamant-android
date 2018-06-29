package im.adamant.android.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.interactors.ChatsInteractor;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.mvp_view.CreateChatView;

import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class CreateChatPresenter extends BasePresenter<CreateChatView>{
    private Router router;
    private ChatsInteractor interactor;

    public CreateChatPresenter(Router router, ChatsInteractor interactor, CompositeDisposable subscriptions) {
        super(subscriptions);
        this.router = router;
        this.interactor = interactor;
    }

    public void onClickCreateNewChat(String address) {
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
        //TODO: Write address verification rules
        if (address == null) {return false;}
        try {
            if (!"U".equalsIgnoreCase(address.substring(0, 1))){return false;}
            if (address.length() < 2){return false;}
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }

        return true;
    }
}
