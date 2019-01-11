package im.adamant.android.interactors;

import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.exceptions.NotFoundPublicKey;
import im.adamant.android.ui.entities.Chat;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class ChatUpdatePublicKeyInteractor {
    private AdamantApiWrapper api;

    public ChatUpdatePublicKeyInteractor(AdamantApiWrapper api) {
        this.api = api;
    }

    public Single<Chat> execute(Chat chat) {
        return api
                .getPublicKey(chat.getCompanionId())
                .flatMap(publicKeyResponse -> {
                    if (publicKeyResponse.isSuccess()){
                        chat.setCompanionPublicKey(publicKeyResponse.getPublicKey());
                        return Flowable.just(chat);
                    } else {
                        return Flowable.error(new NotFoundPublicKey("Not found public key for address: " + chat.getCompanionId()));
                    }
                })
                .firstOrError();
    }

}
