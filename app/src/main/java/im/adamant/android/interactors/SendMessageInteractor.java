package im.adamant.android.interactors;

import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.Encryptor;
import im.adamant.android.core.entities.Account;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.UnnormalizedTransactionMessage;
import im.adamant.android.core.exceptions.MessageTooShortException;
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.core.exceptions.NotEnoughAdamantBalanceException;
import im.adamant.android.helpers.PublicKeyStorage;
import im.adamant.android.core.requests.ProcessTransaction;
import im.adamant.android.core.responses.TransactionWasProcessed;
import com.goterl.lazycode.lazysodium.utils.KeyPair;

import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import im.adamant.android.ui.messages_support.processors.MessageProcessor;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
public class SendMessageInteractor {
    private AdamantApiWrapper api;

    private Encryptor encryptor;
    private PublicKeyStorage publicKeyStorage;

    public SendMessageInteractor(AdamantApiWrapper api, Encryptor encryptor, PublicKeyStorage publicKeyStorage) {
        this.api = api;
        this.encryptor = encryptor;
        this.publicKeyStorage = publicKeyStorage;
    }

    public <T extends AbstractMessage> Single<TransactionWasProcessed> sendMessage(MessageProcessor<T> messageProcessor, T message){

        if (!api.isAuthorized()){return Single.error(new NotAuthorizedException("Not authorized"));}

        KeyPair keyPair = api.getKeyPair();
        Account account = api.getAccount();

        long currentMessageCost = messageProcessor.calculateMessageCostInAdamant(message);
        if (currentMessageCost > account.getBalance()){
            return Single.error(
                    new NotEnoughAdamantBalanceException(
                            "Not enough adamant. Cost:" + currentMessageCost + ". Balance:" + account.getBalance()
                    )
            );
        }

        return Single
                .fromCallable(() -> publicKeyStorage.getPublicKey(message.getCompanionId()))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .flatMap((publicKey) -> messageProcessor.buildTransactionMessage(message, publicKey))
                .flatMap((unnormalizedTransactionMessage -> Single.fromPublisher(
                        api.getNormalizedTransaction(unnormalizedTransactionMessage)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.computation())
                )))
                .flatMap((transactionWasNormalized -> {
                    if (transactionWasNormalized.isSuccess()) {
                        Transaction transaction = transactionWasNormalized.getTransaction();
                        transaction.setSenderId(account.getAddress());

                        transaction.setSignature(
                                encryptor.createTransactionSignature(
                                        transaction,
                                        keyPair
                                )
                        );

                        return Single.just(transaction);
                    } else {
                        return Single.error(new Exception(transactionWasNormalized.getError()));
                    }
                }))
                .flatMap(transaction -> Single.fromPublisher(
                        api.processTransaction(new ProcessTransaction(transaction))
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.computation())
                ));
    }
}
