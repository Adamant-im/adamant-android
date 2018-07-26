package im.adamant.android.presenters;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import im.adamant.android.Screens;
import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.interactors.ChatsInteractor;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.mvp_view.ChatsView;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import ru.terrakok.cicerone.Router;

@InjectViewState
public class ChatsPresenter extends BasePresenter<ChatsView> {
    private Router router;
    private ChatsInteractor interactor;

    private Disposable syncSubscription;

    private final PublishSubject<Void> updateSubject = PublishSubject
            .create();

    public ChatsPresenter(Router router, ChatsInteractor interactor, CompositeDisposable subscriptions) {
        super(subscriptions);
        this.router = router;
        this.interactor = interactor;
    }

    @Override
    public void attachView(ChatsView view) {
        super.attachView(view);

        syncSubscription = interactor
                .synchronizeWithBlockchain()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError((error) -> {
                    Log.e("ERR", error.getClass().getSimpleName());
                    if (error instanceof NotAuthorizedException){
                        router.navigateTo(Screens.LOGIN_SCREEN);
                    } else {
                        router.showSystemMessage(error.getMessage());
                    }

                    Log.e("Chats", error.getMessage(), error);
                })
                .doOnComplete(
                        () -> {
                            getViewState().showChats(interactor.getChatList());
                        }
                )
                .retryWhen((retryHandler) -> retryHandler.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS, TimeUnit.SECONDS))
                .repeatWhen((completed) -> completed.delay(AdamantApi.SYNCHRONIZE_DELAY_SECONDS, TimeUnit.SECONDS))
                .subscribe();

        subscriptions.add(syncSubscription);
    }

    @Override
    public void detachView(ChatsView view) {
        super.detachView(view);

        if (syncSubscription != null){
            syncSubscription.dispose();
        }
    }

    public void onChatWasSelected(Chat chat){
        router.navigateTo(Screens.MESSAGES_SCREEN, chat);
    }

    public void onClickCreateNewChatButton() {
        router.navigateTo(Screens.CREATE_CHAT_SCREEN);
    }

}
