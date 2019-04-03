package im.adamant.android.interactors;

import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import im.adamant.android.BuildConfig;
import im.adamant.android.Constants;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.exceptions.InvalidValueForKeyValueStorage;
import im.adamant.android.core.kvs.ApiKvsProvider;
import im.adamant.android.helpers.KvsHelper;
import im.adamant.android.helpers.ChatsStorage;
import im.adamant.android.ui.entities.Contact;
import io.reactivex.Completable;
import io.reactivex.Flowable;

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
                .get(Constants.KVS_CONTACT_LIST)
                .doOnNext(transaction -> {
                    int timestamp = transaction.getTimestamp();
                    try {
                        HashMap<String, Contact> contacts = kvsHelper.transformFromTransaction(
                                true,
                                transaction,
                                new TypeToken<HashMap<String, Contact>>() {
                                }.getType()
                        );

                        chatsStorage.refreshContacts(contacts, timestamp);
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }

                })
                .onErrorReturnItem(new Transaction<>())
                .ignoreElements()
                .timeout(BuildConfig.DEFAULT_OPERATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
}
