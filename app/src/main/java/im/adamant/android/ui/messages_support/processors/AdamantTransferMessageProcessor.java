package im.adamant.android.ui.messages_support.processors;

import com.goterl.lazycode.lazysodium.utils.KeyPair;

import java.math.BigDecimal;

import im.adamant.android.BuildConfig;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.Encryptor;
import im.adamant.android.core.entities.Account;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.TransactionMessage;
import im.adamant.android.core.entities.UnnormalizedTransactionMessage;
import im.adamant.android.core.entities.transaction_assets.NotUsedAsset;
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.core.exceptions.NotEnoughAdamantBalanceException;
import im.adamant.android.core.requests.ProcessTransaction;
import im.adamant.android.core.responses.TransactionWasProcessed;
import im.adamant.android.helpers.BalanceConvertHelper;
import im.adamant.android.helpers.PublicKeyStorage;
import im.adamant.android.ui.messages_support.entities.AdamantTransferMessage;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class AdamantTransferMessageProcessor extends AbstractMessageProcessor<AdamantTransferMessage> {

    public AdamantTransferMessageProcessor(AdamantApiWrapper api, Encryptor encryptor, PublicKeyStorage publicKeyStorage) {
        super(api, encryptor, publicKeyStorage);
    }

    @Override
    public long calculateMessageCostInAdamant(AdamantTransferMessage message) {
        long fee = BalanceConvertHelper.MULTIPLIER.multiply(new BigDecimal(BuildConfig.ADM_TRANSFER_FEE)).longValue();
        long amount = convertAmount(message.getAmount());
        return amount + fee;
    }

    @Override
    public Single<UnnormalizedTransactionMessage> buildTransactionMessage(AdamantTransferMessage message, String recipientPublicKey) {
        return null;
    }

    @Override
    public Single<TransactionWasProcessed> sendMessage(AdamantTransferMessage message) {

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

        Transaction<NotUsedAsset> transaction = new Transaction<>();
        transaction.setAmount(convertAmount(message.getAmount()));
        transaction.setType(Transaction.SEND);
        transaction.setSenderId(account.getAddress());
        transaction.setSenderPublicKey(account.getPublicKey());
        transaction.setRecipientId(message.getCompanionId());
        transaction.setTimestamp(api.getEpoch() - api.getServerTimeDelta());

        String signature = encryptor.createTransactionSignature(transaction, keyPair);

        transaction.setSignature(signature);

        ProcessTransaction processTransaction = new ProcessTransaction(transaction);


        return api
                .sendAdmTransferTransaction(processTransaction)
                .singleOrError();
    }

    private long convertAmount(BigDecimal amount) {
        BigDecimal messageAmount = (amount == null) ? BigDecimal.ZERO : amount;
        return messageAmount.multiply(BalanceConvertHelper.MULTIPLIER).longValue();
    }
}
