package im.adamant.android.currencies;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import im.adamant.android.R;
import im.adamant.android.core.AdamantApi;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.transaction_assets.NotUsedAsset;
import im.adamant.android.helpers.BalanceConvertHelper;
import im.adamant.android.helpers.ChatsStorage;
import im.adamant.android.ui.adapters.CurrencyTransfersAdapter;
import im.adamant.android.ui.entities.Chat;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class AdamantCurrencyInfoDriver implements CurrencyInfoDriver {
    private AdamantApiWrapper api;
    private ChatsStorage chatsStorage;

    public AdamantCurrencyInfoDriver(AdamantApiWrapper api) {
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
    public SupportedCurrencyType getCurrencyType() {
        return SupportedCurrencyType.ADM;
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
        return api
                .getAdamantTransactions(Transaction.SEND, AdamantApi.ORDER_BY_TIMESTAMP_DESC)
                .flatMap(transactionList -> {
                    if (transactionList.isSuccess()){
                        return Flowable.just(transactionList.getTransactions());
                    } else {
                        return Flowable.error(new Exception(transactionList.getError()));
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
