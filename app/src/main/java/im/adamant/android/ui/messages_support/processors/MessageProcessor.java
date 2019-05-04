package im.adamant.android.ui.messages_support.processors;

import java.util.HashMap;

import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.UnnormalizedTransactionMessage;
import im.adamant.android.core.entities.transaction_assets.TransactionAsset;
import im.adamant.android.core.responses.TransactionWasProcessed;
import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface MessageProcessor<T extends AbstractMessage> {
    long calculateMessageCostInAdamant(T message);
    Single<UnnormalizedTransactionMessage> buildTransactionMessage(T message, String recipientPublicKey);
    Single<Transaction<? extends TransactionAsset>> buildNormalizedTransaction(T message);
    Single<TransactionWasProcessed> sendMessage(T message);
    Single<TransactionWasProcessed> sendTransaction(Single<Transaction<? extends TransactionAsset>> transactionSource);
}
