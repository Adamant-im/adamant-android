package com.dremanovich.adamant_android.ui.mappers;

import com.dremanovich.adamant_android.core.entities.Transaction;
import com.dremanovich.adamant_android.core.helpers.interfaces.AuthorizationStorage;
import com.dremanovich.adamant_android.ui.entities.Chat;
import com.dremanovich.adamant_android.ui.entities.Message;

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
