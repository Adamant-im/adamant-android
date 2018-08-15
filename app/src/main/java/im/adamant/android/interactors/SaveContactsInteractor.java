package im.adamant.android.interactors;

import im.adamant.android.core.kvs.ApiKvsProvider;
import im.adamant.android.rx.ChatsStorage;

public class SaveContactsInteractor {
    private ChatsStorage chatsStorage;
    private ApiKvsProvider apiKvsProvider;

    public SaveContactsInteractor(ApiKvsProvider apiKvsProvider, ChatsStorage chatsStorage) {
        this.apiKvsProvider = apiKvsProvider;
        this.chatsStorage = chatsStorage;
    }

    public void execute(){
//        chatsStorage.
    }
}
