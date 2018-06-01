package com.dremanovich.adamant_android.ui.mappers;

import com.dremanovich.adamant_android.core.encryption.Encryptor;
import com.dremanovich.adamant_android.core.entities.Transaction;
import com.dremanovich.adamant_android.core.helpers.interfaces.AuthorizationStorage;
import com.dremanovich.adamant_android.core.helpers.interfaces.PublicKeyStorage;
import com.dremanovich.adamant_android.ui.entities.Message;

import io.reactivex.functions.Function;

public class TransactionToMessageMapper implements Function<Transaction, Message> {
    private Encryptor encryptor;
    private PublicKeyStorage publicKeyStorage;
    private AuthorizationStorage authorizationStorage;

    public TransactionToMessageMapper(
            Encryptor encryptor,
            PublicKeyStorage publicKeyStorage,
            AuthorizationStorage authorizationStorage
    ) {
        this.encryptor = encryptor;
        this.publicKeyStorage = publicKeyStorage;
        this.authorizationStorage = authorizationStorage;
    }

    //TODO: Refactor this. The length of the method is too long.
    @Override
    public Message apply(Transaction transaction) throws Exception {
        Message message = null;

        if (authorizationStorage.getKeyPair() == null || authorizationStorage.getAccount() == null){
            throw new Exception("You are not authorized.");
        }

        String ownAddress = authorizationStorage.getAccount().getAddress();
        String ownSecretKey = authorizationStorage.getKeyPair().getSecretKeyString().toLowerCase();

        if (transaction.getAsset() == null){ return message; }
        if (transaction.getAsset().getChat() == null){ return message; }

        boolean iRecipient = ownAddress.equalsIgnoreCase(transaction.getRecipientId());

        String encryptedMessage = transaction.getAsset().getChat().getMessage();
        String encryptedNonce = transaction.getAsset().getChat().getOwnMessage();
        String senderPublicKey = transaction.getSenderPublicKey();

        String decryptedMessage = "";

        if (iRecipient){
            decryptedMessage = encryptor.decryptMessage(
                    encryptedMessage,
                    encryptedNonce,
                    senderPublicKey,
                    ownSecretKey
            );
        } else {
            String recipientPublicKey = publicKeyStorage.getPublicKey(transaction.getRecipientId());
            decryptedMessage = encryptor.decryptMessage(
                    encryptedMessage,
                    encryptedNonce,
                    recipientPublicKey,
                    ownSecretKey
            );
        }

        String companionId = (iRecipient) ? transaction.getSenderId() : transaction.getRecipientId();

        message = new Message();
        message.setMessage(decryptedMessage);
        message.setiSay(ownAddress.equals(transaction.getSenderId()));
        message.setDate(transaction.getTimestamp());
        message.setCompanionId(companionId);
        message.setProcessed(true);
        message.setTransactionId(transaction.getId());

        return message;
    }
}
