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
import im.adamant.android.helpers.AdamantAddressProcessor;
import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import im.adamant.android.ui.messages_support.entities.AdamantBasicMessage;
import im.adamant.android.ui.messages_support.SupportedMessageTypes;

import com.github.curioustechizen.ago.RelativeTimeTextView;


public class AdamantBasicMessageViewHolder extends AbstractMessageViewHolder {
    private ImageView processedView;
    private TextView messageView;
    private RelativeTimeTextView dateView;
    private ConstraintLayout constraintLayout;
    private ConstraintSet constraintSet = new ConstraintSet();
    private AdamantAddressProcessor adamantAddressProcessor;


    public AdamantBasicMessageViewHolder(Context context, View itemView, AdamantAddressProcessor adamantAddressProcessor) {
        super(context, itemView);
        this.adamantAddressProcessor = adamantAddressProcessor;

        constraintLayout = itemView.findViewById(R.id.message_item);
        TransitionManager.beginDelayedTransition(constraintLayout);
        constraintSet.clone(constraintLayout);
        processedView = itemView.findViewById(R.id.list_item_message_processed);
        messageView = itemView.findViewById(R.id.list_item_message_text);
        messageView.setMovementMethod(LinkMovementMethod.getInstance());
        dateView = itemView.findViewById(R.id.list_item_message_date);
    }

    @Override
    public void bind(AbstractMessage message) {

        if (message != null){

            if (message.getSupportedType() != SupportedMessageTypes.ADAMANT_BASIC){
                emptyView();
                return;
            }

            AdamantBasicMessage basicMessage = (AdamantBasicMessage)message;

            messageView.setText(
                basicMessage.getHtmlText(adamantAddressProcessor)
            );

            dateView.setReferenceTime(message.getDate());

            if (message.isProcessed()){
                processedView.setImageResource(R.drawable.ic_processed);
            } else {
                processedView.setImageResource(R.drawable.ic_not_processed);
            }

            if (message.isiSay()){
                iToldLayoutModification();
            } else {
                companionToldModification();
            }

        } else {
            emptyView();
        }
    }

    private void iToldLayoutModification(){
        constraintSet.setHorizontalBias(R.id.cardView, .9f);
        constraintSet.applyTo(itemView.findViewById(R.id.message_item));
    }

    private void companionToldModification(){
        constraintSet.setHorizontalBias(R.id.cardView, .1f);
        constraintSet.applyTo(itemView.findViewById(R.id.message_item));
    }

    private void emptyView() {
        messageView.setText("");
        processedView.setImageResource(R.drawable.ic_not_processed);
        dateView.setReferenceTime(System.currentTimeMillis());
    }
}
