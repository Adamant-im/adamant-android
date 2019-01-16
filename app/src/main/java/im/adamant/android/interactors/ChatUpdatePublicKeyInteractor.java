package im.adamant.android.interactors;

import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.ui.entities.Chat;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class ChatUpdatePublicKeyInteractor {
    private AdamantApiWrapper api;
    private CompositeDisposable subscriptions = new CompositeDisposable();

    public ChatUpdatePublicKeyInteractor(AdamantApiWrapper api) {
        this.api = api;
    }

    public void execute(Chat chat) {
        Disposable subscribe = api
                .getPublicKey(chat.getCompanionId())
                .firstOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(publicKeyResponse -> {
                    if (publicKeyResponse.isSuccess()) {
                        chat.setCompanionPublicKey(publicKeyResponse.getPublicKey());
                    } else {
                        LoggerHelper.e("PublicKey", publicKeyResponse.getError());
                    }
                });
        subscriptions.add(subscribe);
    }

    @Override
    protected void finalize() throws Throwable {
        subscriptions.dispose();
        subscriptions.clear();
        super.finalize();
    }
}
