package im.adamant.android.interactors.chats;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import im.adamant.android.BuildConfig;
import im.adamant.android.Constants;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.kvs.ApiKvsProvider;
import im.adamant.android.helpers.KvsHelper;
import im.adamant.android.ui.entities.Contact;
import io.reactivex.Completable;
import io.reactivex.Flowable;

public class ContactsSource {
    private ApiKvsProvider apiKvsProvider;
    private KvsHelper kvsHelper;

    private int lastTimestamp = 0;

    public ContactsSource(ApiKvsProvider apiKvsProvider, KvsHelper kvsHelper) {
        this.apiKvsProvider = apiKvsProvider;
        this.kvsHelper = kvsHelper;
    }

    public Flowable<HashMap<String, Contact>> execute() {
        return apiKvsProvider
                .get(Constants.KVS_CONTACT_LIST)
                .filter(transaction -> transaction.getTimestamp() != lastTimestamp)
                .doOnNext(transaction -> lastTimestamp = transaction.getTimestamp())
                .flatMap(transaction -> {
                    try {
                        HashMap<String, Contact> contacts = kvsHelper.transformFromTransaction(
                                true,
                                transaction,
                                new TypeToken<HashMap<String, Contact>>() {
                                }.getType()
                        );

                        return Flowable.just(contacts);
                    } catch (Exception ex){
                        return Flowable.error(ex);
                    }
                })
                .timeout(BuildConfig.DEFAULT_OPERATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
}
