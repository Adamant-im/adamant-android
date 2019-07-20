package im.adamant.android.ui.mappers;

import android.util.Pair;

import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.helpers.PublicKeyStorage;
import im.adamant.android.ui.entities.Chat;
import io.reactivex.functions.Function;

public class TransactionToChatMapper implements Function<Pair<String, Transaction<?>>, Chat> {
    private AdamantApiWrapper api;

    public TransactionToChatMapper(AdamantApiWrapper api) {
        this.api = api;
    }

    @Override
    public Chat apply(Pair<String, Transaction<?>> transactionPair) throws Exception {
        Transaction<?> transaction = transactionPair.second;
        String ownAddress = api.getAccount().getAddress();
        boolean iRecipient = ownAddress.equalsIgnoreCase(transaction.getRecipientId());

        String companionId = (iRecipient) ? transaction.getSenderId() : transaction.getRecipientId();
        String companionPublicKey = transactionPair.first;

        Chat chat = new Chat();
        chat.setCompanionId(companionId);
        chat.setTitle(companionId);
        chat.setCompanionPublicKey(companionPublicKey);

        return chat;
    }


}
