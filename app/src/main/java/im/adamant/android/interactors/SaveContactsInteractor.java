package im.adamant.android.interactors;

import java.util.Map;

import im.adamant.android.Constants;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.transaction_assets.TransactionStateAsset;
import im.adamant.android.core.exceptions.EncryptionException;
import im.adamant.android.core.kvs.ApiKvsProvider;
import im.adamant.android.helpers.KvsHelper;
import im.adamant.android.interactors.chats.ChatsStorage;
import im.adamant.android.ui.entities.Contact;
import io.reactivex.Completable;

public class SaveContactsInteractor {
    private ChatsStorage chatsStorage;
    private ApiKvsProvider apiKvsProvider;
    private KvsHelper kvsHelper;

    public SaveContactsInteractor(ApiKvsProvider apiKvsProvider, ChatsStorage chatsStorage, KvsHelper kvsHelper) {
        this.apiKvsProvider = apiKvsProvider;
        this.chatsStorage = chatsStorage;
        this.kvsHelper = kvsHelper;
    }

    public Completable execute() {
        Map<String, Contact> contacts = chatsStorage.getContacts();

        Transaction<TransactionStateAsset> contactListTransaction = null;
        try {
            contactListTransaction = kvsHelper.transformToTransaction(
                    Constants.KVS_CONTACT_LIST,
                    true,
                    contacts
            );
        } catch (EncryptionException e) {
            return Completable.error(e);
        }

        return apiKvsProvider.put(contactListTransaction);
    }
}
