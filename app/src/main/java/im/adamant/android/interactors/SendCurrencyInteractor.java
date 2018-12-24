package im.adamant.android.interactors;

import java.math.BigDecimal;

import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.core.responses.TransactionWasProcessed;
import im.adamant.android.helpers.ChatsStorage;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.builders.MessageBuilder;
import im.adamant.android.ui.messages_support.entities.AdamantTransferMessage;
import im.adamant.android.ui.messages_support.factories.AdamantTransferMessageFactory;
import im.adamant.android.ui.messages_support.factories.MessageFactoryProvider;
import im.adamant.android.ui.messages_support.processors.MessageProcessor;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class SendCurrencyInteractor {
    private AdamantApiWrapper api;
    private ChatsStorage chatsStorage;
    private MessageFactoryProvider messageFactoryProvider;

    public SendCurrencyInteractor(AdamantApiWrapper api, ChatsStorage chatsStorage, MessageFactoryProvider messageFactoryProvider) {
        this.chatsStorage = chatsStorage;
        this.messageFactoryProvider = messageFactoryProvider;
        this.api = api;
    }

    public Single<TransactionWasProcessed> sendCurrency(String recipientAddress, String text, BigDecimal amount, SupportedWalletFacadeType facadeType) {
        Single<TransactionWasProcessed> result = Single.error(new Exception("Unimplemented message type"));

        if (!api.isAuthorized()) {return Single.error(new NotAuthorizedException("Not authorized"));}
        if (BigDecimal.ZERO.compareTo(amount) >= 0) {return Single.error(new Exception("Amount less or equal 0"));}


        String pKey = api.getAccount().getPublicKey();

        try {
            switch (facadeType) {
                case ADM: {
                    result = sendAdamant(recipientAddress, amount, pKey);
                }
            }
        } catch (Exception ex) {
            result = Single.error(ex);
        }

        return result;
    }

    private Single<TransactionWasProcessed> sendAdamant(String recipientAddress, BigDecimal amount, String pKey) throws Exception {
        AdamantTransferMessageFactory factory = (AdamantTransferMessageFactory) messageFactoryProvider.getFactoryByType(SupportedMessageListContentType.ADAMANT_TRANSFER_MESSAGE);
        MessageBuilder<AdamantTransferMessage> messageBuilder = factory.getMessageBuilder();
        AdamantTransferMessage message = messageBuilder.build(
                null,
                "",
                true,
                System.currentTimeMillis(),
                recipientAddress,
                pKey
        );

        message.setAmount(amount);

        chatsStorage.addMessageToChat(message);

        MessageProcessor<AdamantTransferMessage> messageProcessor = factory.getMessageProcessor();
        return messageProcessor
                .sendMessage(message)
                .doAfterSuccess(transactionWasProcessed -> {
                    if (transactionWasProcessed.isSuccess()){
                        message.setTransactionId(transactionWasProcessed.getTransactionId());
                        message.setProcessed(true);
                    }
                });
    }
}
