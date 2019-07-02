package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import im.adamant.android.ui.entities.TransferDetails;

public interface TransferDetailsView extends MvpView {
    @StateStrategyType(AddToEndSingleStrategy.class)
    void showTransferDetails(UITransferDetails details);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void setLoading(boolean loading);

    public static class UITransferDetails{
        public static enum Direction{
            SENT, RECEIVED;
        }

        protected String id;
        protected String amount;
        protected String fee;
        protected String date;
        protected String fromId, toId;
        protected long confirmations;
        protected String fromAddress;
        protected String toAddress;
        protected TransferDetails.STATUS status;
        protected String explorerLink;
        protected boolean haveChat;
        protected Direction direction;

        public Direction getDirection() {
            return direction;
        }

        public UITransferDetails setDirection(Direction direction) {
            this.direction = direction;
            return this;
        }

        public boolean haveChat() {
            return haveChat;
        }

        public UITransferDetails setHaveChat(boolean haveChat) {
            this.haveChat = haveChat;
            return this;
        }

        public TransferDetails.STATUS getStatus() {
            return status;
        }

        public UITransferDetails setStatus(TransferDetails.STATUS status) {
            this.status = status;
            return this;
        }

        public String getId() {
            return id;
        }

        public UITransferDetails setId(String id) {
            this.id = id;
            return this;
        }

        public String getAmount() {
            return amount;
        }

        public UITransferDetails setAmount(String amount) {
            this.amount = amount;
            return this;
        }

        public String getFee() {
            return fee;
        }

        public UITransferDetails setFee(String fee) {
            this.fee = fee;
            return this;
        }

        public String getDate() {
            return date;
        }

        public UITransferDetails setDate(String date) {
            this.date = date;
            return this;
        }

        public String getFromId() {
            return fromId;
        }

        public UITransferDetails setFromId(String fromId) {
            this.fromId = fromId;
            return this;
        }

        public String getToId() {
            return toId;
        }

        public UITransferDetails setToId(String toId) {
            this.toId = toId;
            return this;
        }

        public long getConfirmations() {
            return confirmations;
        }

        public UITransferDetails setConfirmations(long confirmations) {
            this.confirmations = confirmations;
            return this;
        }

        public String getFromAddress() {
            return fromAddress;
        }

        public UITransferDetails setFromAddress(String fromAddress) {
            this.fromAddress = fromAddress;
            return this;
        }

        public String getToAddress() {
            return toAddress;
        }

        public UITransferDetails setToAddress(String toAddress) {
            this.toAddress = toAddress;
            return this;
        }

        public String getExplorerLink() {
            return explorerLink;
        }

        public UITransferDetails setExplorerLink(String explorerLink) {
            this.explorerLink = explorerLink;
            return this;
        }
    }
}
