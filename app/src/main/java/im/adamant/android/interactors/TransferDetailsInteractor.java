package im.adamant.android.interactors;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import im.adamant.android.AdamantApplication;
import im.adamant.android.R;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.interactors.chats.ChatsStorage;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
import im.adamant.android.interactors.wallets.WalletFacade;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.entities.TransferDetails;
import io.reactivex.Flowable;

import static im.adamant.android.ui.mvp_view.TransferDetailsView.UITransferDetails;

public class TransferDetailsInteractor {
    private Map<SupportedWalletFacadeType, WalletFacade> wallets;
    private ChatsStorage chatsStorage;
    private AdamantApiWrapper api;


    public TransferDetailsInteractor(AdamantApiWrapper api, Map<SupportedWalletFacadeType, WalletFacade> wallets, ChatsStorage chatsStorage) {
        this.wallets = wallets;
        this.chatsStorage = chatsStorage;
        this.api = api;
    }

    private final DecimalFormat decimalFormatter = new DecimalFormat("#.###");
    private final DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.getDefault());

    @Nullable
    private String getAddressName(String id) {
        Chat chat = chatsStorage.findChatByCompanionId(id);
        if (chat == null) {
            return null;
        }
        String title = chat.getTitle();
        if (id.equals(title)) {
            return null;
        } else {
            if (accountId.equals(title)) {
                title = AdamantApplication.appCtx.getString(R.string.transaction_address_me);
            }
            return title;
        }
    }

    private String accountId;

    public Flowable<UITransferDetails> getTransferDetailsInteractor(String id,String abbr) {
        SupportedWalletFacadeType supportedCurrencyType = SupportedWalletFacadeType.valueOf(abbr);
        WalletFacade walletFacade = wallets.get(supportedCurrencyType);
        return walletFacade.getTransferDetails(id)
                .map(this::getUiTransferDetails);
    }

    @NotNull
    private UITransferDetails getUiTransferDetails(TransferDetails details) {
        accountId = api.getAccount().getAddress();

        UITransferDetails result = new UITransferDetails()
                .setAmount(decimalFormatter.format(details.getAmount()))
                .setConfirmations(details.getConfirmations())
                .setFee(decimalFormatter.format(details.getFee()))
                .setFromId(details.getFromId())
                .setFromAddress(getAddressName(details.getFromId()))
                .setToId(details.getToId())
                .setToAddress(getAddressName(details.getToId()))
                .setDate(dateFormat.format(new Date(details.getUnixTransferDate())))
                .setStatus(details.getStatus());

        if (accountId.equals(details.getFromId())) {
            result.setDirection(UITransferDetails.Direction.SENT);
        } else if (accountId.equals(details.getToId())) {
            result.setDirection(UITransferDetails.Direction.RECEIVED);
        } else {
            throw new IllegalArgumentException("User is not related to this " +
                    "transaction");
        }

        if (result.getDirection() == UITransferDetails.Direction.SENT) {
            result.setHaveChat(chatsStorage.
                    findChatByCompanionId(details.getToId()) != null);
        } else if (result.getDirection() == UITransferDetails.Direction.RECEIVED) {
            result.setHaveChat(chatsStorage.
                    findChatByCompanionId(details.getFromId()) != null);
        } else {
            throw new IllegalArgumentException("Unknown direction");
        }

        return result;
    }
}
