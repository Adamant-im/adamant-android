package im.adamant.android.ui.messages_support.processors;

import com.goterl.lazycode.lazysodium.utils.KeyPair;

import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.Encryptor;
import im.adamant.android.core.entities.Account;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.UnnormalizedTransactionMessage;
import im.adamant.android.core.entities.transaction_assets.TransactionAsset;
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.core.exceptions.NotEnoughAdamantBalanceException;
import im.adamant.android.core.requests.ProcessTransaction;
import im.adamant.android.core.responses.TransactionWasProcessed;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.helpers.PublicKeyStorage;
import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public abstract class AbstractMessageProcessor<T extends AbstractMessage> implements MessageProcessor<T> {
    protected AdamantApiWrapper api;

    protected Encryptor encryptor;
    protected PublicKeyStorage publicKeyStorage;

    public AbstractMessageProcessor(AdamantApiWrapper api, Encryptor encryptor, PublicKeyStorage publicKeyStorage) {
        this.api = api;
        this.encryptor = encryptor;
        this.publicKeyStorage = publicKeyStorage;
    }

    @Override
    public Single<Transaction<? extends TransactionAsset>> buildNormalizedTransaction(T message) {
        //TODO: Rewrite the error handling so that any error is displayed under the message.
        if (!api.isAuthorized()){return Single.error(new NotAuthorizedException("Not authorized"));}

        KeyPair keyPair = api.getKeyPair();
        Account account = api.getAccount();

        long currentMessageCost = this.calculateMessageCostInAdamant(message);
        if (currentMessageCost > account.getBalance()){
            return Single.error(
                    new NotEnoughAdamantBalanceException(
                            "Not enough adamant. Cost:" + currentMessageCost + ". Balance:" + account.getBalance()
                    )
            );
        }

        try {
            return publicKeyStorage.findPublicKey(message.getCompanionId())
                    .singleOrError()
                    .observeOn(Schedulers.computation())
                    .flatMap(publicKey -> buildTransactionMessage(message, publicKey))
                    .flatMap((unnormalizedTransactionMessage -> Single.fromPublisher(
                            api.getNormalizedTransaction(unnormalizedTransactionMessage)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(Schedulers.computation())
                    )))
                    .flatMap((transactionWasNormalized -> {
                        if (transactionWasNormalized.isSuccess()) {
                            Transaction<? extends TransactionAsset> transaction = transactionWasNormalized.getTransaction();
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
                    }));
        } catch (Exception ex) {
            ex.printStackTrace();
            return Single.error(ex);
        }
    }

    @Override
    public Single<TransactionWasProcessed> sendMessage(T message) {
        return buildNormalizedTransaction(message)
            .flatMap(transaction -> Single.fromPublisher(
                    api.processTransaction(new ProcessTransaction(transaction))
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.computation())
            ))
            .doAfterSuccess(transactionWasProcessed -> {
                if (transactionWasProcessed.isSuccess()) {
                    message.setTransactionId(transactionWasProcessed.getTransactionId());
                    message.setStatus(AbstractMessage.Status.DELIVERED);
                }
            })
            .doOnError(throwable -> {
                message.setStatus(AbstractMessage.Status.NOT_SENDED);
                message.setError(throwable.getMessage());
            });
    }

    @Override
    public Single<TransactionWasProcessed> sendTransaction(Single<Transaction<? extends TransactionAsset>> transactionSource) {
        return transactionSource
                .flatMap(transaction -> Single.fromPublisher(
                        api.processTransaction(new ProcessTransaction(transaction))
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.computation())
                ));
    }
}
