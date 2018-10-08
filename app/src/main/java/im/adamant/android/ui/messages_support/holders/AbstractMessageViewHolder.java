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
import im.adamant.android.helpers.AdamantAddressProcessor;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import im.adamant.android.ui.messages_support.entities.MessageListContent;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class AbstractMessageViewHolder extends AbstractMessageListContentViewHolder {
    protected final static SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

    protected int parentPadding;
    protected int avatarMargin;
    protected int avatarSize;

    protected ImageView processedView;
    protected TextView timeView;
    protected View avatarBlockView;
    protected View messageBlockView;
    protected ImageView avatarView;
    protected FrameLayout contentBlock;

    protected ConstraintLayout constraintLayout;
    protected ConstraintSet constraintSet = new ConstraintSet();
    protected CompositeDisposable compositeDisposable = new CompositeDisposable();

    protected AdamantAddressProcessor adamantAddressProcessor;
    protected Avatar avatar;

    public AbstractMessageViewHolder(
            Context context,
            View itemView,
            AdamantAddressProcessor adamantAddressProcessor,
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

        avatarBlockView = itemView.findViewById(R.id.list_item_message_avatar_block);
        messageBlockView = itemView.findViewById(R.id.list_item_message_card);

        avatarView = itemView.findViewById(R.id.list_item_message_avatar);
        timeView = itemView.findViewById(R.id.list_item_message_time);
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

        avatarView.setImageBitmap(null);
        if (abstractMessage.getOwnerPublicKey() != null){
            Disposable avatarSubscription = avatar
                    .build(abstractMessage.getOwnerPublicKey(), avatarSize)
                    .subscribe(
                            avatar -> avatarView.setImageBitmap(avatar),
                            error -> LoggerHelper.e("messageHolder", error.getMessage(), error)
                    );
            compositeDisposable.add(avatarSubscription);
        } else {
            avatarView.setImageResource(R.mipmap.ic_launcher_foreground);
        }

        timeView.setText(timeFormatter.format(abstractMessage.getDate()));

        if (abstractMessage.isiSay()){
            iToldLayoutModification();
        } else {
            companionToldModification();
        }

    }

    private void iToldLayoutModification(){
        constraintSet.clear(avatarBlockView.getId(), ConstraintSet.START);
        constraintSet.connect(avatarBlockView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, parentPadding);

        constraintSet.clear(messageBlockView.getId(), ConstraintSet.START);
        constraintSet.clear(messageBlockView.getId(), ConstraintSet.END);

        constraintSet.connect(messageBlockView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, parentPadding);
        constraintSet.connect(messageBlockView.getId(), ConstraintSet.END, avatarBlockView.getId(), ConstraintSet.START, avatarMargin);

        constraintSet.applyTo(constraintLayout);
    }

    private void companionToldModification(){
        constraintSet.connect(avatarBlockView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, parentPadding);
        constraintSet.clear(avatarBlockView.getId(), ConstraintSet.END);

        constraintSet.clear(messageBlockView.getId(), ConstraintSet.START);
        constraintSet.clear(messageBlockView.getId(), ConstraintSet.END);

        constraintSet.connect(messageBlockView.getId(), ConstraintSet.START, avatarBlockView.getId(), ConstraintSet.END, avatarMargin);
        constraintSet.connect(messageBlockView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, parentPadding);

        constraintSet.applyTo(constraintLayout);
    }

    protected void emptyView() {
        processedView.setImageResource(R.drawable.ic_not_processed);
        timeView.setText("00:00");
    }

    protected void displayProcessedStatus(ImageView processedView, AbstractMessage message){
        if (message.isProcessed()){
            processedView.setImageResource(R.drawable.ic_processed);
        } else {
            processedView.setImageResource(R.drawable.ic_not_processed);
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
