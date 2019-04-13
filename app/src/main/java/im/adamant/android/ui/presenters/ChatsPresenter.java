package im.adamant.android.ui.presenters;

import com.arellomobile.mvp.InjectViewState;

import java.util.concurrent.TimeUnit;

import im.adamant.android.Screens;
import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.chats.ChatInteractor;
import im.adamant.android.interactors.chats.ChatsStorage;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.mvp_view.ChatsView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class ChatsPresenter extends BasePresenter<ChatsView> {
    private Router router;
    private ChatInteractor chatInteractor;
    private ChatsStorage chatsStorage;


    public ChatsPresenter(
            Router router,
            ChatInteractor chatInteractor,
            ChatsStorage chatsStorage
    ) {
        this.router = router;
        this.chatInteractor = chatInteractor;
        this.chatsStorage = chatsStorage;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();

        Disposable disposable = chatInteractor
                .loadChats()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    //TODO: Refactor update subscription
                    Disposable updateDisposable = chatInteractor
                            .update()
                            .observeOn(AndroidSchedulers.mainThread())
                            .doAfterSuccess(newItemsCount -> {
                                if (newItemsCount > 0) {
                                    getViewState().showChats(chatsStorage.getChatList());
                                }
                            })
                            .doOnError(
                                error -> {
                                    if (error instanceof NotAuthorizedException) {
                                        router.navigateTo(Screens.SPLASH_SCREEN);
                                    } else {
                                        router.showSystemMessage(error.getMessage());
                                    }

                                    LoggerHelper.e("Chats", error.getMessage(), error);
                                }
                            )
                            .repeatWhen((completed) -> completed.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS, TimeUnit.SECONDS))
                            .subscribe();

                    subscriptions.add(updateDisposable);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            ChatsStorage lc = chatsStorage;
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
