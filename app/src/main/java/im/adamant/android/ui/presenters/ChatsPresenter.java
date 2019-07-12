package im.adamant.android.ui.presenters;

import com.arellomobile.mvp.InjectViewState;

import java.util.concurrent.TimeUnit;

import im.adamant.android.Screens;
import im.adamant.android.core.AdamantApi;
import im.adamant.android.helpers.AnimationUtils;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.interactors.chats.ChatInteractor;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.mvp_view.ChatsView;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class ChatsPresenter extends ProtectedBasePresenter<ChatsView> {
    private ChatInteractor chatInteractor;

    public ChatsPresenter(
            Router router,
            AccountInteractor accountInteractor,
            ChatInteractor chatInteractor
    ) {
        super(router, accountInteractor);
        this.chatInteractor = chatInteractor;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();

        getViewState().progress(false);

        Disposable disposable = chatInteractor
                .loadChats()
                .flatMap(Flowable::toList)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> {
                    LoggerHelper.e(getClass().getSimpleName(), throwable.getMessage(), throwable);
                    router.showSystemMessage(throwable.getMessage());
                })
                .subscribe(chats -> {
                    chatInteractor.loadContacts();
                    getViewState().showChats(chats);
                    getViewState().progress(false);
                    Disposable updatedDisposable = chatInteractor
                            .update()
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnError(throwable -> {
                                LoggerHelper.e(getClass().getSimpleName(), throwable.getMessage(), throwable);
                                router.showSystemMessage(throwable.getMessage());
                            })
                            .doAfterSuccess((newItemsCount) -> {
                                if (newItemsCount > 0) {
                                    Disposable updateDisposable = chatInteractor
                                            .loadChats()
                                            .flatMap(Flowable::toList)
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .doOnError(throwable -> {
                                                LoggerHelper.e(getClass().getSimpleName(), throwable.getMessage(), throwable);
                                                router.showSystemMessage(throwable.getMessage());
                                            })
                                            .subscribe(chatsUpdated -> {
                                                getViewState().showChats(chatsUpdated);
                                            });
                                    subscriptions.add(updateDisposable);
                                }
                            })
                            .repeatWhen((completed) -> completed.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS, TimeUnit.SECONDS))
                            .onErrorReturnItem(1L)
                            .subscribe();

                    subscriptions.add(updatedDisposable);
                });

        subscriptions.add(disposable);
    }


//    @Override
//    public void attachView(ChatsView view) {
//        super.attachView(view);
//
//        getViewState().showChats(chatsStorage.getChatList());
//    }

    public void onChatWasSelected(Chat chat) {
        router.navigateTo(Screens.MESSAGES_SCREEN, chat.getCompanionId());
    }

    public void onClickCreateNewChatButton(AnimationUtils.RevealAnimationSetting animationSetting) {
        router.navigateTo(Screens.CREATE_CHAT_SCREEN, animationSetting);
    }

}
