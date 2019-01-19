package im.adamant.android.ui.messages_support.entities;

import android.content.Context;
import android.text.Spanned;

import java.math.BigDecimal;

import im.adamant.android.markdown.AdamantMarkdownProcessor;
import im.adamant.android.helpers.HtmlHelper;

public class EthereumTransferMessage extends AbstractMessage {
    private BigDecimal amount;
    private String comment;
    private String ethereumTransactionId;
    private transient Spanned htmlComment;

    @Override
    public String getShortedMessage(Context context, int preferredLimit) {
        return shorteningString(comment, preferredLimit);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getComment() {
        return comment;
    }

    public Spanned getHtmlComment(AdamantMarkdownProcessor adamantAddressProcessor) {
        if (htmlComment == null) {
            try {
                htmlComment = HtmlHelper.fromHtml(adamantAddressProcessor.getHtmlString(comment));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return htmlComment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getEthereumTransactionId() {
        return ethereumTransactionId;
    }

    public void setEthereumTransactionId(String ethereumTransactionId) {
        this.ethereumTransactionId = ethereumTransactionId;
    }
}
