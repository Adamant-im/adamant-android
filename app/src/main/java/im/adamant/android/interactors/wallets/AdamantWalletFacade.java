package im.adamant.android.interactors.wallets;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import im.adamant.android.BuildConfig;
import im.adamant.android.R;
import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.transaction_assets.NotUsedAsset;
import im.adamant.android.core.entities.transaction_assets.TransactionAsset;
import im.adamant.android.core.responses.TransactionDetailsResponse;
import im.adamant.android.core.responses.TransactionList;
import im.adamant.android.helpers.BalanceConvertHelper;
import im.adamant.android.interactors.chats.ChatsStorage;
import im.adamant.android.ui.entities.ADMTransferDetails;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.entities.CurrencyTransferEntity;
import im.adamant.android.ui.entities.TransferDetails;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class AdamantWalletFacade implements WalletFacade {
    private AdamantApiWrapper api;
    private ChatsStorage chatsStorage;

    private boolean isReceivedTransactionList = false;
    private boolean isEmptyTransactionList = false;
    private int maxHeight = 1;

    public AdamantWalletFacade(AdamantApiWrapper api) {
        this.api = api;
    }

    @Override
    public BigDecimal getBalance() {
        if (api.isAuthorized()){
            return BalanceConvertHelper.convert(api.getAccount().getUnconfirmedBalance());
        } else {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public String getAddress() {
        String address = "";
        if (api.isAuthorized()){
            address = api.getAccount().getAddress();
        }

        return address;
    }

    @Override
    public SupportedWalletFacadeType getCurrencyType() {
        return SupportedWalletFacadeType.ADM;
    }

    @Override
    public String getTitle() {
        return "ADAMANT WALLET";
    }

    @Override
    public int getPrecision() {
        return 3;
    }

    @Override
    public int getBackgroundLogoResource() {
        return R.drawable.ic_adm_line;
    }

    @Override
    public void setChatStorage(ChatsStorage chatStorage) {
        this.chatsStorage = chatStorage;
    }

    @Override
    public Single<List<CurrencyTransferEntity>> getLastTransfers() {
        String myAddress = api.getAccount().getAddress();
        return mapTransactionsToTransfers(
                    api.getAdamantAllFinanceTransactions(AdamantApi.ORDER_BY_TIMESTAMP_DESC)
                )
                .map(list -> {
                    List<CurrencyTransferEntity> transfers = new ArrayList<>();

                    for(Transaction transaction : list){
                        if (transaction.getHeight() > maxHeight) { maxHeight = transaction.getHeight(); }
                        transfers.add(mapTransactionToTransfer(transaction, myAddress));
                    }
                    return transfers;
                })
                .timeout(BuildConfig.DEFAULT_OPERATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);

    }

    @Override
    public Flowable<CurrencyTransferEntity> getNewTransfers() {
        String myAddress = api.getAccount().getAddress();
        return mapTransactionsToTransfers(
                    Flowable
                            .defer(() -> Flowable.just(maxHeight + 1))
                            .flatMap(height -> api.getAdamantAllFinanceTransactions(height, 0, AdamantApi.ORDER_BY_TIMESTAMP_DESC))
                 )
                 .toFlowable()
                 .flatMapIterable(list -> list)
                 .doOnNext(transaction -> {
                     if (transaction.getHeight() > maxHeight) {
                         maxHeight = transaction.getHeight();
                     }
                 })
                 .map(transaction -> mapTransactionToTransfer(transaction, myAddress))
                 .timeout(BuildConfig.DEFAULT_OPERATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);

    }

    @Override
    public Flowable<CurrencyTransferEntity> getNextTransfers(int offset) {
        String myAddress = api.getAccount().getAddress();
        return mapTransactionsToTransfers(
                    api.getAdamantAllFinanceTransactions(1, offset, AdamantApi.ORDER_BY_TIMESTAMP_DESC)
                )
                .toFlowable()
                .flatMapIterable(list -> list)
                .map(transaction -> mapTransactionToTransfer(transaction, myAddress))
                .timeout(BuildConfig.DEFAULT_OPERATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    public boolean isAvailableAirdropLink() {
        boolean isZeroBalance = (getBalance().compareTo(BigDecimal.ZERO) == 0);
        boolean isEmptyTransactionList = (isReceivedTransactionList && this.isEmptyTransactionList);
        if (isZeroBalance && isEmptyTransactionList) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getAirdropLinkResource() {
        return R.string.free_token_url;
    }

    @Override
    public String getAirdropLinkString() {
        return "";
    }

    @Override
    public boolean isSupportFundsSending() {
        return true;
    }

    @Override
    public Flowable<BigDecimal> getFee() {
        return Flowable.just(new BigDecimal(BuildConfig.ADM_TRANSFER_FEE)
                .setScale(getPrecision(), RoundingMode.HALF_EVEN));
    }

    @Override
    public String getCurrencyAddress(String adamantAddress, String adamantPublicKey) {
        return adamantAddress;
    }

    @Override
    public int getIconForEditText() {
        return R.drawable.ic_adm_token;
    }

    @Override
    public boolean isSupportComment() {
        return false;
    }

    @Override
    public Flowable<TransferDetails> getTransferDetails(String id) {
        return  mapTransactionDetailsResponseToTransfer(api.getTransactionDetails(id))
                .timeout(BuildConfig.DEFAULT_OPERATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    private TransferDetails transferDetailsFromTransaction(Transaction t){
        return new ADMTransferDetails()
                .setAmount(BalanceConvertHelper.convert(t.getAmount()))
                .setConfirmations(t.getConfirmations())
                .setId(t.getId())
                .setUnixTransferDate(t.getUnixTimestamp())
                .setFromId(t.getSenderId())
                .setToId(t.getRecipientId())
                .setFee(BalanceConvertHelper.convert(t.getFee()));
    }

    private Flowable<TransferDetails> mapTransactionDetailsResponseToTransfer(Flowable<TransactionDetailsResponse> response){
        return response.flatMap(transferDetails->{
                Transaction t = transferDetails.getTransaction();
                if (transferDetails.isSuccess()){
                    return Flowable.just(transferDetailsFromTransaction(t));
                } else {
                    return Flowable.error(new Exception(transferDetails.getError()));
                }
        });
    }

    private String getTransferTitle(boolean iRecipient, Transaction<NotUsedAsset> transaction) {
        String title = "";
        if (chatsStorage == null){ return "";}
        String address = "";
        if (iRecipient){
            address = transaction.getSenderId();
        } else {
            address = transaction.getRecipientId();
        }

        Chat chat = chatsStorage.findChatByCompanionId(address);
        if (chat != null){
            title = chat.getTitle();
        }
        return title;
    }

    private Single<List<Transaction<? super TransactionAsset>>> mapTransactionsToTransfers(Flowable<TransactionList> transactionListFlowable) {
        return transactionListFlowable
                .flatMap(transactionList -> {
                    if (transactionList.isSuccess()){
                        return Flowable.just(transactionList.getTransactions());
                    } else {
                        return Flowable.error(new Exception(transactionList.getError()));
                    }
                })
                .doOnNext(list -> {
                    isReceivedTransactionList = true;
                    if (list.size() == 0) {
                        isEmptyTransactionList = true;
                    }
                })
                .singleOrError();
    }

    private CurrencyTransferEntity mapTransactionToTransfer(Transaction transaction, String myAddress) {
        CurrencyTransferEntity entity = new CurrencyTransferEntity();
        entity.setUnixTransferDate(transaction.getUnixTimestamp());
        entity.setPrecision(getPrecision());
        entity.setAmount(
                BalanceConvertHelper.convert(
                        transaction.getAmount()
                )
        );

        boolean iRecipient = myAddress.equalsIgnoreCase(transaction.getRecipientId());

        if (iRecipient){
            entity.setDirection(CurrencyTransferEntity.Direction.RECEIVE);
            entity.setAddress(transaction.getSenderId());
        } else {
            entity.setDirection(CurrencyTransferEntity.Direction.SEND);
            entity.setAddress(transaction.getRecipientId());
        }

        entity.setContactName(getTransferTitle(iRecipient, transaction));

        return entity;
    }

}
