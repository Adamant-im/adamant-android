package com.dremanovich.adamant_android.ui.mappers;

import com.dremanovich.adamant_android.core.encryption.Encryptor;
import com.dremanovich.adamant_android.core.entities.Transaction;
import com.dremanovich.adamant_android.core.helpers.interfaces.AuthorizationStorage;
import com.dremanovich.adamant_android.core.helpers.interfaces.PublicKeyStorage;
import com.dremanovich.adamant_android.core.responses.TransactionList;
import com.dremanovich.adamant_android.ui.entities.Chat;
import com.dremanovich.adamant_android.ui.entities.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.functions.Function;

public class TransactionsToChatsMapper implements Function<TransactionList, List<Chat>> {
    private Encryptor encryptor;
    private PublicKeyStorage publicKeyStorage;
    private AuthorizationStorage authorizationStorage;

    public TransactionsToChatsMapper(
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
    public List<Chat> apply(TransactionList transactions) throws Exception {
        HashMap<String,Chat> chats = new HashMap<>();

        if (!transactions.isSuccess()){
            throw new Exception(transactions.getError());
        }

        if (authorizationStorage.getKeyPair() == null || authorizationStorage.getAccount() == null){
            throw new Exception("You are not authorized.");
        }

        String ownAddress = authorizationStorage.getAccount().getAddress();
        String ownSecretKey = authorizationStorage.getKeyPair().getSecretKeyString().toLowerCase();

        for(final Transaction transaction : transactions.getTransactions()){
            if (transaction.getAsset() == null){ continue; }
            if (transaction.getAsset().getChat() == null){ continue; }

            boolean iRecipient = ownAddress.equalsIgnoreCase(transaction.getRecipientId());

            String encryptedMessage = transaction.getAsset().getChat().getMessage();
            String encryptedNonce = transaction.getAsset().getChat().getOwnMessage();
            String senderPublicKey = transaction.getSenderPublicKey();

            String decriptedMessage = "";

            if (iRecipient){
                decriptedMessage = encryptor.decryptMessage(
                        encryptedMessage,
                        encryptedNonce,
                        senderPublicKey,
                        ownSecretKey
                );
            } else {
                String recipientPublicKey = publicKeyStorage.getPublicKey(transaction.getRecipientId());
                decriptedMessage = encryptor.decryptMessage(
                        encryptedMessage,
                        encryptedNonce,
                        recipientPublicKey,
                        ownSecretKey
                );
            }

            String interlocutorId = (iRecipient) ? transaction.getSenderId() : transaction.getRecipientId();

            Chat chat = chats.get(interlocutorId);

            if (chat == null){
                chat = new Chat();
                chat.setInterlocutorId(interlocutorId);
                chats.put(interlocutorId, chat);
            }

            Message message = new Message();
            message.setMessage(decriptedMessage);
            message.setiSay(ownAddress.equals(transaction.getSenderId()));
            chat.getMessages().add(message);

        }

        return new ArrayList<>(chats.values());
    }
}
