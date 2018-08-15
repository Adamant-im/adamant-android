package im.adamant.android.interactors;

import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.transaction_assets.TransactionStateAsset;
import im.adamant.android.core.kvs.ApiKvsProvider;
import im.adamant.android.helpers.KvsHelper;
import im.adamant.android.rx.ChatsStorage;
import im.adamant.android.ui.entities.Contact;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.flowables.GroupedFlowable;

public class GetContactsInteractor {
    private ChatsStorage chatsStorage;
    private ApiKvsProvider apiKvsProvider;
    private KvsHelper kvsHelper;

    public GetContactsInteractor(ApiKvsProvider apiKvsProvider, ChatsStorage chatsStorage, KvsHelper kvsHelper) {
        this.apiKvsProvider = apiKvsProvider;
        this.chatsStorage = chatsStorage;
        this.kvsHelper = kvsHelper;
    }

    public Completable execute() {
        return apiKvsProvider
                .get("contact_list")
                .doOnNext(transaction -> {
                    int timestamp = transaction.getTimestamp();
                    HashMap<String, Contact> contacts = kvsHelper.transformFromTransaction(
                            true,
                            transaction,
                            new TypeToken<HashMap<String, Contact>>() {
                            }.getType()
                    );

                    chatsStorage.refreshContacts(contacts, timestamp);
                })
                .ignoreElements();
    }
}
