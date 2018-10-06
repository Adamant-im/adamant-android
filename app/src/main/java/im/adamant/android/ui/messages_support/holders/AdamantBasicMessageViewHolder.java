package im.adamant.android.ui.messages_support.holders;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.transition.TransitionManager;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import im.adamant.android.R;
import im.adamant.android.avatars.AvatarGenerator;
import im.adamant.android.helpers.AdamantAddressProcessor;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.ui.messages_support.entities.AdamantBasicMessage;
import im.adamant.android.ui.messages_support.SupportedMessageListContentType;
import im.adamant.android.ui.messages_support.entities.MessageListContent;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class AdamantBasicMessageViewHolder extends AbstractMessageListContentViewHolder {
    private final static SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

    private int parentPadding;
    private int avatarMargin;
    private float avatarSize;

    private ImageView processedView;
    private TextView messageView;
    private TextView timeView;
    private View avatarBlockView;
    private View messageBlockView;
    private ImageView avatarView;

    private ConstraintLayout constraintLayout;
    private ConstraintSet constraintSet = new ConstraintSet();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private AdamantAddressProcessor adamantAddressProcessor;
    private AvatarGenerator avatarGenerator;

    public AdamantBasicMessageViewHolder(
            Context context,
            View itemView,
            AdamantAddressProcessor adamantAddressProcessor,
            AvatarGenerator avatarGenerator
    ) {
        super(context, itemView);
        this.adamantAddressProcessor = adamantAddressProcessor;
        this.avatarGenerator = avatarGenerator;

        constraintLayout = itemView.findViewById(R.id.message_item);
        TransitionManager.beginDelayedTransition(constraintLayout);
        constraintSet.clone(constraintLayout);

        parentPadding = (int)context.getResources().getDimension(R.dimen.list_item_message_padding);
        avatarMargin = (int)context.getResources().getDimension(R.dimen.list_item_message_avatar_margin);
        avatarSize = context.getResources().getDimension(R.dimen.list_item_avatar_size);

        avatarBlockView = itemView.findViewById(R.id.list_item_message_avatar_block);
        messageBlockView = itemView.findViewById(R.id.list_item_message_card);

        avatarView = itemView.findViewById(R.id.list_item_message_avatar);
        processedView = itemView.findViewById(R.id.list_item_message_processed);
        messageView = itemView.findViewById(R.id.list_item_message_text);
        messageView.setMovementMethod(LinkMovementMethod.getInstance());
        timeView = itemView.findViewById(R.id.list_item_message_time);
    }

    @Override
    public void bind(MessageListContent message) {

        if (message != null){

            if (message.getSupportedType() != SupportedMessageListContentType.ADAMANT_BASIC){
                emptyView();
                return;
            }

            AdamantBasicMessage basicMessage = (AdamantBasicMessage)message;

            messageView.setText(
                basicMessage.getHtmlText(adamantAddressProcessor)
            );

            if (basicMessage.getAvatar() == null){
                if (basicMessage.getOwnerPublicKey() != null){
                    Disposable avatarSubscription = avatarGenerator
                            .buildAvatar(basicMessage.getOwnerPublicKey(), avatarSize, context, true)
                            .subscribe(
                                    bitmap -> {
                                        avatarView.setImageBitmap(bitmap);
                                        basicMessage.setAvatar(bitmap);
                                    },
                                    error -> {
                                        LoggerHelper.e("chatHolder", error.getMessage(), error);
                                    }
                            );
                    compositeDisposable.add(avatarSubscription);
                } else {
                    avatarView.setImageResource(R.mipmap.ic_launcher_foreground);
                }
            } else {
                avatarView.setImageBitmap(basicMessage.getAvatar());
            }

            //TODO: Entity must return date
            timeView.setText(timeFormatter.format(new Date(basicMessage.getTimestamp())));

            if (basicMessage.isProcessed()){
                processedView.setImageResource(R.drawable.ic_processed);
            } else {
                processedView.setImageResource(R.drawable.ic_not_processed);
            }

            if (basicMessage.isiSay()){
                iToldLayoutModification();
            } else {
                companionToldModification();
            }

        } else {
            emptyView();
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

    private void emptyView() {
        messageView.setText("");
        processedView.setImageResource(R.drawable.ic_not_processed);
        timeView.setText("00:00");
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
