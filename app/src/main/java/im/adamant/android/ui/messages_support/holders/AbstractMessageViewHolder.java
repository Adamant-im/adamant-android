package im.adamant.android.ui.messages_support.holders;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.transition.TransitionManager;
import im.adamant.android.R;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.markdown.AdamantMarkdownProcessor;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import im.adamant.android.ui.messages_support.entities.MessageListContent;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class AbstractMessageViewHolder extends AbstractMessageListContentViewHolder {
    protected final static SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

    protected int parentPadding;
    protected int avatarMargin;
    protected int avatarSize;

    protected ImageView processedView;
    protected TextView timeView;
    protected TextView errorView;
//    protected View avatarBlockView;
    protected View messageBlockView;
//    protected ImageView avatarView;
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
        avatarMargin = (int)context.getResources().getDimension(R.dimen.list_item_message_avatar_margin);
        avatarSize = (int) context.getResources().getDimension(R.dimen.list_item_avatar_size);

//        avatarBlockView = itemView.findViewById(R.id.list_item_message_avatar_block);
        messageBlockView = itemView.findViewById(R.id.list_item_message_card);

        processedView = itemView.findViewById(R.id.list_item_message_processed);

//        avatarView = itemView.findViewById(R.id.list_item_message_avatar);
        timeView = itemView.findViewById(R.id.list_item_message_time);
        errorView = itemView.findViewById(R.id.list_item_message_error_text);
        contentBlock = itemView.findViewById(R.id.list_item_message_content);
    }

    @Override
    public void bind(MessageListContent message) {
        boolean isCorruptedMessage = (message == null) || (message.getSupportedType() == SupportedMessageListContentType.SEPARATOR);

        if (isCorruptedMessage){
            emptyView();
            return;
        }

        AbstractMessage abstractMessage = (AbstractMessage) message;

//        avatarView.setImageBitmap(null);
//        if (abstractMessage.getOwnerPublicKey() != null){
//            Disposable avatarSubscription = avatar
//                    .build(abstractMessage.getOwnerPublicKey(), avatarSize)
//                    .subscribe(
//                            avatar -> avatarView.setImageBitmap(avatar),
//                            error -> LoggerHelper.e("messageHolder", error.getMessage(), error)
//                    );
//            compositeDisposable.add(avatarSubscription);
//        } else {
//            avatarView.setImageResource(R.mipmap.ic_launcher_foreground);
//        }
//
        timeView.setText(timeFormatter.format(abstractMessage.getDate()));
        errorView.setText(abstractMessage.getError());

        if (abstractMessage.isiSay()){
            iToldLayoutModification();
        } else {
            companionToldModification();
        }

    }

    private void iToldLayoutModification(){
        constraintSet.clear(messageBlockView.getId(), ConstraintSet.START);
        constraintSet.clear(messageBlockView.getId(), ConstraintSet.END);
        constraintSet.connect(messageBlockView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, parentPadding);

        constraintSet.applyTo(constraintLayout);

//        constraintSet.clear(avatarBlockView.getId(), ConstraintSet.START);
//        constraintSet.connect(avatarBlockView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, parentPadding);

//        constraintSet.clear(messageBlockView.getId(), ConstraintSet.START);
//        constraintSet.clear(messageBlockView.getId(), ConstraintSet.END);
//
//        constraintSet.connect(messageBlockView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, parentPadding);
//        constraintSet.connect(messageBlockView.getId(), ConstraintSet.END, avatarBlockView.getId(), ConstraintSet.START, avatarMargin);

//        constraintSet.clear(errorView.getId(), ConstraintSet.START);
//        constraintSet.clear(errorView.getId(), ConstraintSet.END);
//
//        constraintSet.connect(errorView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, parentPadding);
//        constraintSet.connect(errorView.getId(), ConstraintSet.END, avatarBlockView.getId(), ConstraintSet.START, avatarMargin);

//        constraintSet.applyTo(constraintLayout);
    }

    private void companionToldModification(){
        constraintSet.clear(messageBlockView.getId(), ConstraintSet.START);
        constraintSet.clear(messageBlockView.getId(), ConstraintSet.END);
        constraintSet.connect(messageBlockView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, parentPadding);

        constraintSet.applyTo(constraintLayout);

//        constraintSet.connect(avatarBlockView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, parentPadding);
//        constraintSet.clear(avatarBlockView.getId(), ConstraintSet.END);

//        constraintSet.clear(messageBlockView.getId(), ConstraintSet.START);
//        constraintSet.clear(messageBlockView.getId(), ConstraintSet.END);

//        constraintSet.connect(messageBlockView.getId(), ConstraintSet.START, avatarBlockView.getId(), ConstraintSet.END, avatarMargin);
//        constraintSet.connect(messageBlockView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, parentPadding);

//        constraintSet.clear(errorView.getId(), ConstraintSet.START);
//        constraintSet.clear(errorView.getId(), ConstraintSet.END);

//        constraintSet.connect(errorView.getId(), ConstraintSet.START, avatarBlockView.getId(), ConstraintSet.END, avatarMargin);
//        constraintSet.connect(errorView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, parentPadding);

//        constraintSet.applyTo(constraintLayout);
    }

    protected void emptyView() {
        processedView.setImageResource(R.drawable.ic_sending);
//        timeView.setText("00:00");
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

    @Override
    protected void finalize() throws Throwable {
        if (compositeDisposable != null){
            compositeDisposable.dispose();
            compositeDisposable.clear();
        }
        super.finalize();
    }
}
