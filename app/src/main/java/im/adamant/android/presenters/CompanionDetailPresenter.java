package im.adamant.android.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import im.adamant.android.rx.ChatsStorage;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.mvp_view.CompanionDetailView;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class CompanionDetailPresenter extends MvpPresenter<CompanionDetailView> {
    private Router router;
    private ChatsStorage chatsStorage;

    private Chat currentChat;

    public CompanionDetailPresenter(Router router, ChatsStorage chatsStorage) {
        this.chatsStorage = chatsStorage;
        this.router = router;
    }

    public void onLoadInfoByChat(String companionId){
        currentChat = chatsStorage.findChatByCompanionId(companionId);
        if (currentChat != null){
            getViewState().showCompanionName(currentChat.getTitle());
        }
    }
}
