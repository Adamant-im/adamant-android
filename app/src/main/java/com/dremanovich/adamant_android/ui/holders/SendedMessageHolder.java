package com.dremanovich.adamant_android.ui.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dremanovich.adamant_android.R;
import com.dremanovich.adamant_android.ui.entities.Message;
import com.github.curioustechizen.ago.RelativeTimeTextView;

public class SendedMessageHolder extends RecyclerView.ViewHolder {
    private ImageView processedView;
    private TextView messageView;
    private RelativeTimeTextView dateView;

    public SendedMessageHolder(View itemView) {
        super(itemView);
        processedView = itemView.findViewById(R.id.list_item_message_processed);
        messageView = itemView.findViewById(R.id.list_item_message_text);
        dateView = itemView.findViewById(R.id.list_item_message_date);
    }

    public void bind(Message message){
        if (message != null){
            messageView.setText(message.getMessage());
            dateView.setReferenceTime(message.getDate());

            if (message.isProcessed()){
                processedView.setImageResource(R.drawable.ic_processed);
            } else {
                processedView.setImageResource(R.drawable.ic_not_processed);
            }

        } else {
            messageView.setText("");
            processedView.setImageResource(R.drawable.ic_not_processed);
            dateView.setReferenceTime(System.currentTimeMillis());
        }
    }
}
