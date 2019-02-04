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
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.helpers.BalanceConvertHelper;
import im.adamant.android.helpers.ChatsStorage;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.entities.CurrencyTransferEntity;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class AdamantWalletFacade implements WalletFacade {
    private AdamantApiWrapper api;
    private ChatsStorage chatsStorage;

    private boolean isReceivedTransactionList = false;
    private boolean isEmptyTransactionList = false;

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
        if (!api.isAuthorized()){
            return Single.error(new NotAuthorizedException("Not Authorized"));
        }

        String myAddress = api.getAccount().getAddress();
        return api
                .getAdamantTransactions(Transaction.SEND, AdamantApi.ORDER_BY_TIMESTAMP_DESC)
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
                .map(list -> {
                    List<CurrencyTransferEntity> transfers = new ArrayList<>();

                    for(Transaction<NotUsedAsset> transaction : list){
                        CurrencyTransferEntity entity = new CurrencyTransferEntity();
                        entity.setPrecision(getPrecision());
                        entity.setAmount(
                                BalanceConvertHelper.convert(
                                        transaction.getAmount()
                                )
                        );
//                        entity.setCurrencyAbbreviation(SupportedCurrencyType.ADM.name());

                        boolean iRecipient = myAddress.equalsIgnoreCase(transaction.getRecipientId());

                        if (iRecipient){
                            entity.setDirection(CurrencyTransferEntity.Direction.RECEIVE);
                            entity.setAddress(transaction.getSenderId());
                        } else {
                            entity.setDirection(CurrencyTransferEntity.Direction.SEND);
                            entity.setAddress(transaction.getRecipientId());
                        }

                        entity.setContactName(getTransferTitle(iRecipient, transaction));

                        transfers.add(entity);
                    }
                    return transfers;
                })
                .first(new ArrayList<>());

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
}
