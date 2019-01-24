package im.adamant.android.ui.messages_support.processors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.goterl.lazycode.lazysodium.utils.KeyPair;

import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.Encryptor;
import im.adamant.android.core.entities.Account;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.TransactionMessage;
import im.adamant.android.core.entities.UnnormalizedTransactionMessage;
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.helpers.PublicKeyStorage;
import im.adamant.android.ui.messages_support.entities.AdamantPushSubscriptionMessage;
import io.reactivex.Single;

public class AdamantPushSubscriptionMessageProcessor extends AbstractMessageProcessor<AdamantPushSubscriptionMessage> {
    private Gson gson;

    public AdamantPushSubscriptionMessageProcessor(GsonBuilder gsonBuilder, AdamantApiWrapper api, Encryptor encryptor, PublicKeyStorage publicKeyStorage) {
        super(api, encryptor, publicKeyStorage);
        this.gson = gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
    }

    @Override
    public long calculateMessageCostInAdamant(AdamantPushSubscriptionMessage message) {
        return AdamantApi.MINIMUM_COST;
    }

    @Override
    public Single<UnnormalizedTransactionMessage> buildTransactionMessage(AdamantPushSubscriptionMessage message, String recipientPublicKey) {
        if (!api.isAuthorized()){return Single.error(new NotAuthorizedException("Not authorized"));}

        KeyPair keyPair = api.getKeyPair();
        Account account = api.getAccount();

        String content = gson.toJson(message);

        return Single
                .defer(() -> Single.just(recipientPublicKey))
                .flatMap((publicKey) -> Single.just(encryptor.encryptMessage(
                        content,
                        publicKey,
                        keyPair.getSecretKeyString().toLowerCase()
                )))
                .flatMap((transactionMessage -> Single.fromCallable(
                        () -> {
                            UnnormalizedTransactionMessage unnormalizedMessage = new UnnormalizedTransactionMessage();
                            unnormalizedMessage.setMessage(transactionMessage.getMessage());
                            unnormalizedMessage.setOwnMessage(transactionMessage.getOwnMessage());
                            unnormalizedMessage.setMessageType(TransactionMessage.SIGNAL_MESSAGE_TYPE);
                            unnormalizedMessage.setType(Transaction.CHAT_MESSAGE);
                            unnormalizedMessage.setPublicKey(keyPair.getPublicKeyString().toLowerCase());
                            unnormalizedMessage.setRecipientId(message.getCompanionId());
                            unnormalizedMessage.setSenderId(account.getAddress());

                            return unnormalizedMessage;
                        }
                )));
    }
}
