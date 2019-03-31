package im.adamant.android.ui.presenters;

import com.arellomobile.mvp.InjectViewState;
import im.adamant.android.Screens;
import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.GetContactsInteractor;
import im.adamant.android.interactors.RefreshChatsInteractor;
import im.adamant.android.helpers.ChatsStorage;
import im.adamant.android.rx.RxTaskManager;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.mvp_view.ChatsView;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class ChatsPresenter extends BasePresenter<ChatsView> {
    private Router router;
    private GetContactsInteractor getContactsInteractor;
    private RefreshChatsInteractor refreshChatsInteractor;
    private ChatsStorage chatsStorage;

    private RxTaskManager taskManager;

    public ChatsPresenter(
            Router router,
            GetContactsInteractor getContactsInteractor,
            RefreshChatsInteractor refreshChatsInteractor,
            ChatsStorage chatsStorage
    ) {
        this.router = router;
        this.getContactsInteractor = getContactsInteractor;
        this.refreshChatsInteractor = refreshChatsInteractor;
        this.chatsStorage = chatsStorage;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();

        Disposable disposable = refreshChatsInteractor
                .execute()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (irrelevant) -> {
                            subscriptions.add(
                                    getContactsInteractor
                                            .execute()
                                            .subscribe()
                            );
                            getViewState().showChats(chatsStorage.getChatList());
                        },
                        (error) -> {
                            if (error instanceof NotAuthorizedException){
                                router.navigateTo(Screens.SPLASH_SCREEN);
                            } else {
                                router.showSystemMessage(error.getMessage());
                            }

                            LoggerHelper.e("Chats", error.getMessage(), error);
                        }
                );

        subscriptions.add(disposable);
    }

    public void onChatWasSelected(Chat chat){
        router.navigateTo(Screens.MESSAGES_SCREEN, chat.getCompanionId());
    }

    public void onClickCreateNewChatButton() {
        router.navigateTo(Screens.CREATE_CHAT_SCREEN);
    }

}
