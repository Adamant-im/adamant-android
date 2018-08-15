package im.adamant.android.interactors;

import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.Encryptor;
import im.adamant.android.core.entities.Account;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.UnnormalizedTransactionMessage;
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.helpers.PublicKeyStorage;
import im.adamant.android.core.requests.ProcessTransaction;
import im.adamant.android.core.responses.TransactionWasProcessed;
import com.goterl.lazycode.lazysodium.utils.KeyPair;

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

    public Single<TransactionWasProcessed> sendMessage(String message, String address){

        if (!api.isAuthorized()){return Single.error(new NotAuthorizedException("Not authorized"));}

        KeyPair keyPair = api.getKeyPair();
        Account account = api.getAccount();

        return Single
                .fromCallable(() -> publicKeyStorage.getPublicKey(address))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .flatMap((publicKey) -> Single.just(encryptor.encryptMessage(
                        message,
                        publicKey,
                        keyPair.getSecretKeyString().toLowerCase()
                )))
                .flatMap((transactionMessage -> Single.fromCallable(
                        () -> {
                            UnnormalizedTransactionMessage unnormalizedMessage = new UnnormalizedTransactionMessage();
                            unnormalizedMessage.setMessage(transactionMessage.getMessage());
                            unnormalizedMessage.setOwnMessage(transactionMessage.getOwnMessage());
                            unnormalizedMessage.setMessageType(1);
                            unnormalizedMessage.setType(8);
                            unnormalizedMessage.setPublicKey(keyPair.getPublicKeyString().toLowerCase());
                            unnormalizedMessage.setRecipientId(address);
                            unnormalizedMessage.setSenderId(account.getAddress());

                            return unnormalizedMessage;
                        }
                )))
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
