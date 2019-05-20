package im.adamant.android.ui.presenters;

import com.arellomobile.mvp.InjectViewState;

import java.util.concurrent.TimeUnit;

import im.adamant.android.Screens;
import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.chats.ChatInteractor;
import im.adamant.android.interactors.chats.ChatsStorage;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.mvp_view.ChatsView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class ChatsPresenter extends ProtectedBasePresenter<ChatsView> {
    private ChatInteractor chatInteractor;
    private ChatsStorage chatsStorage;

    public ChatsPresenter(
            Router router,
            AccountInteractor accountInteractor,
            ChatInteractor chatInteractor,
            ChatsStorage chatsStorage
    ) {
        super(router, accountInteractor);
        this.chatInteractor = chatInteractor;
        this.chatsStorage = chatsStorage;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();

        Disposable disposable = chatInteractor
                .loadChats()
                .ignoreElements()
                .andThen(chatInteractor.loadContacts())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> {
                    LoggerHelper.e(getClass().getSimpleName(), throwable.getMessage(), throwable);
                    router.showSystemMessage(throwable.getMessage());
                })
                .doOnComplete(() -> getViewState().showChats(chatsStorage.getChatList()))
                .subscribe(() -> {
                    Disposable updatedDisposabled = chatInteractor
                            .update()
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnError(throwable -> {
                                LoggerHelper.e(getClass().getSimpleName(), throwable.getMessage(), throwable);
                                router.showSystemMessage(throwable.getMessage());
                            })
                            .doAfterSuccess((newItemsCount) -> {
                                if (newItemsCount > 0) {
                                    getViewState().showChats(chatsStorage.getChatList());
                                }
                            })
                            .repeatWhen((completed) -> completed.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS, TimeUnit.SECONDS))
                            .onErrorReturnItem(1L)
                            .subscribe();

                    subscriptions.add(updatedDisposabled);
                });

        subscriptions.add(disposable);
    }


//    @Override
//    public void attachView(ChatsView view) {
//        super.attachView(view);
//
//        getViewState().showChats(chatsStorage.getChatList());
//    }

    public void onChatWasSelected(Chat chat){
        router.navigateTo(Screens.MESSAGES_SCREEN, chat.getCompanionId());
    }

    public void onClickCreateNewChatButton() {
        router.navigateTo(Screens.CREATE_CHAT_SCREEN);
    }

}
