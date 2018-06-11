package im.adamant.android.ui.mappers;

import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.helpers.interfaces.AuthorizationStorage;
import im.adamant.android.ui.entities.Chat;

import io.reactivex.functions.Function;

public class TransactionToChatMapper implements Function<Transaction, Chat> {
    private AuthorizationStorage authorizationStorage;

    public TransactionToChatMapper(AuthorizationStorage authorizationStorage) {
        this.authorizationStorage = authorizationStorage;
    }

    @Override
    public Chat apply(Transaction transaction) throws Exception {
        String ownAddress = authorizationStorage.getAccount().getAddress();
        boolean iRecipient = ownAddress.equalsIgnoreCase(transaction.getRecipientId());

        String companionId = (iRecipient) ? transaction.getSenderId() : transaction.getRecipientId();

        Chat chat = new Chat();
        chat.setCompanionId(companionId);

        return chat;
    }
}
