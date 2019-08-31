package im.adamant.android.ui.presenters;

import androidx.annotation.MainThread;

import com.arellomobile.mvp.InjectViewState;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import im.adamant.android.BuildConfig;
import im.adamant.android.Screens;
import im.adamant.android.core.AdamantApi;
import im.adamant.android.helpers.AnimationUtils;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.chats.ChatInteractor;
import im.adamant.android.interactors.chats.ChatsStorage;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.mvp_view.ChatsView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class ChatsPresenter extends ProtectedBasePresenter<ChatsView> {
    private ChatInteractor chatInteractor;
    private ChatsStorage chatsStorage;
    private List<Chat> chatList = Collections.emptyList();

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

    @MainThread
    private void showChats(List<Chat> chats) {
        this.chatList = chats;
        getViewState().showChats(chatList);
    }

    private void subscribeToUpdates() {
        Disposable updatedDisposable = chatInteractor
                .update()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> {
                    LoggerHelper.e(getClass().getSimpleName(), throwable.getMessage(), throwable);
                    router.showSystemMessage(throwable.getMessage());
                })
                .doAfterSuccess((newItemsCount) -> {
                    if (newItemsCount > 0) {
                        showChats(chatsStorage.getChatList());
                    }
                })
                .repeatWhen((completed) -> completed.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS, TimeUnit.SECONDS))
                .onErrorReturnItem(1L)
                .subscribe();

        subscriptions.add(updatedDisposable);

        Disposable contactsSubscription = chatInteractor
                .loadContacts()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> showChats(chatsStorage.getChatList()))
                .repeatWhen((completed) -> completed.delay(BuildConfig.UPDATE_CONTACTS_SECONDS_DELAY, TimeUnit.SECONDS))
                .retryWhen((completed) -> completed.delay(BuildConfig.UPDATE_CONTACTS_SECONDS_DELAY, TimeUnit.SECONDS))
                .subscribe();

        subscriptions.add(contactsSubscription);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();

        showChats(chatsStorage.getChatList());
        if (chatsStorage.getChatList().isEmpty()) {
            getViewState().progress(true);
            Disposable disposable = chatInteractor.loadMoreChats()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .share()
                    .subscribe(chats -> {
                        getViewState().progress(false);
                        getViewState().showChats(chats);
                        subscribeToUpdates();
                    });

            subscriptions.add(disposable);
        } else {
            getViewState().progress(false);
            subscribeToUpdates();
        }
    }

    public void onLoadMore() {
        if (chatInteractor.haveMoreChats()) {
            getViewState().progress(true);
            Disposable disposable = chatInteractor.loadMoreChats()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(chats -> {
                        getViewState().progress(false);
                        showChats(chats);
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe();
            subscriptions.add(disposable);
        }
    }

    @Override
    public void attachView(ChatsView view) {
        super.attachView(view);

        if (this.chatList != null) {
            //TODO: Think about redesign chastorage. Because new ArrayList created every time when a view is attached.
            chatsStorage.updateLastMessages();
            this.chatList = chatsStorage.getChatList();
            getViewState().showChats(this.chatList);
        }
    }

    public void onChatWasSelected(Chat chat) {
        router.navigateTo(Screens.MESSAGES_SCREEN, chat.getCompanionId());
    }

    public void onClickCreateNewChatButton(AnimationUtils.RevealAnimationSetting animationSetting) {
        router.navigateTo(Screens.CREATE_CHAT_SCREEN, animationSetting);
    }

}
