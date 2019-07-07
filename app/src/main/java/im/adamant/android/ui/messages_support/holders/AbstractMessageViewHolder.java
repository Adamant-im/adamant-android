package im.adamant.android.ui.messages_support.holders;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;
import im.adamant.android.R;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.markdown.AdamantMarkdownProcessor;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import im.adamant.android.ui.messages_support.entities.MessageListContent;
import io.reactivex.disposables.CompositeDisposable;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public abstract class AbstractMessageViewHolder extends AbstractMessageListContentViewHolder {
    protected final static SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

    protected int parentPadding;
    protected int sameSenderTopPadding;
    protected int notSameSenderTopPadding;
    protected int lastMessagePadding;
    protected int avatarMargin;
    protected int avatarSize;

    protected ImageView processedView;
    protected TextView timeView;
    protected TextView errorView;
    protected View messageBlockView;
    protected FrameLayout contentBlock;

    protected ConstraintLayout constraintLayout;
    protected ConstraintSet constraintSet = new ConstraintSet();
    protected CompositeDisposable compositeDisposable = new CompositeDisposable();

    protected AdamantMarkdownProcessor adamantAddressProcessor;
    protected Avatar avatar; //TODO: Remove

    public AbstractMessageViewHolder(
            Context context,
            View itemView,
            AdamantMarkdownProcessor adamantAddressProcessor,
            Avatar avatar
    ) {
        super(context, itemView);
        this.adamantAddressProcessor = adamantAddressProcessor;
        this.avatar = avatar;

        constraintLayout = itemView.findViewById(R.id.message_item);
        TransitionManager.beginDelayedTransition(constraintLayout);
        constraintSet.clone(constraintLayout);

        parentPadding = (int)context.getResources().getDimension(R.dimen.list_item_message_padding);
        lastMessagePadding=(int)context.getResources().getDimension(R.dimen.activity_messages_last_message_padding);
        avatarMargin = (int)context.getResources().getDimension(R.dimen.list_item_message_avatar_margin);
        avatarSize = (int) context.getResources().getDimension(R.dimen.list_item_avatar_size);
        sameSenderTopPadding = (int) context.getResources().getDimension(R.dimen.activity_messages_same_sender_padding);
        notSameSenderTopPadding = (int) context.getResources().getDimension(R.dimen.activity_messages_not_same_sender_padding);

        messageBlockView = itemView.findViewById(R.id.list_item_message_card);

        processedView = itemView.findViewById(R.id.list_item_message_processed);

        timeView = itemView.findViewById(R.id.list_item_message_time);
        errorView = itemView.findViewById(R.id.list_item_message_error_text);
        contentBlock = itemView.findViewById(R.id.list_item_message_content);
    }

    @Override
    public void bind(MessageListContent message, boolean isNextMessageWithSameSender, boolean isLastMessage) {
        boolean isCorruptedMessage = (message == null) || (message.getSupportedType() == SupportedMessageListContentType.SEPARATOR);

        if (isCorruptedMessage){
            emptyView();
            return;
        }

        AbstractMessage abstractMessage = (AbstractMessage) message;

        timeView.setText(timeFormatter.format(abstractMessage.getDate()));

        if (abstractMessage.isiSay()){
            iToldLayoutModification(isNextMessageWithSameSender);
        } else {
            companionToldModification(isNextMessageWithSameSender);
        }

        boolean isHideError = (abstractMessage.getError() == null || abstractMessage.getError().isEmpty());

        if (isLastMessage) {
            constraintLayout.setPadding(0, 0, 0, lastMessagePadding);
        } else {
            constraintLayout.setPadding(0, 0, 0, 0);
        }

        //VERY IMPORTANT: This code must be placed under function which will change constraints, otherwise setVisibility doesn't work.
        if (isHideError) {
            errorView.setVisibility(View.GONE);
        } else {
            errorView.setText(abstractMessage.getError());
            errorView.setVisibility(View.VISIBLE);
        }
    }

    private void iToldLayoutModification(boolean isNextMessageWithSameSender) {
        constraintSet.clear(messageBlockView.getId(), ConstraintSet.START);
        constraintSet.clear(messageBlockView.getId(), ConstraintSet.END);
        constraintSet.connect(messageBlockView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, parentPadding);

        displayIsSameSenderPadding(isNextMessageWithSameSender);

        constraintSet.applyTo(constraintLayout);

    }

    private void companionToldModification(boolean isNextMessageWithSameSender) {
        constraintSet.clear(messageBlockView.getId(), ConstraintSet.START);
        constraintSet.clear(messageBlockView.getId(), ConstraintSet.END);
        constraintSet.connect(messageBlockView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, parentPadding);

        displayIsSameSenderPadding(isNextMessageWithSameSender);

        constraintSet.applyTo(constraintLayout);
    }

    protected void emptyView() {
        processedView.setImageResource(R.drawable.ic_sending);
    }

    protected void displayProcessedStatus(AbstractMessage message){
        if (message.getStatus() == null) { return; }
        switch (message.getStatus()) {
            case DELIVERED: {
                processedView.setImageResource(R.drawable.ic_delivered);
            }
            break;
            case SENDING_AND_VALIDATION: {
                processedView.setImageResource(R.drawable.ic_sending);
            }
            break;
            case INVALIDATED:
            case NOT_SENDED: {
                processedView.setImageResource(R.drawable.ic_not_sended);
            }
        }
    }

    protected void displayIsSameSenderPadding(boolean isNextMessageWithSameSender) {
        if (isNextMessageWithSameSender) {
            constraintSet.connect(messageBlockView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, sameSenderTopPadding);
        } else {
            constraintSet.connect(messageBlockView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, notSameSenderTopPadding);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (compositeDisposable != null){
            compositeDisposable.dispose();
            compositeDisposable.clear();
        }
        super.finalize();
    }
}
