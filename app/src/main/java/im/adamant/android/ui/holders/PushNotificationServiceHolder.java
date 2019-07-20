package im.adamant.android.ui.holders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import im.adamant.android.R;
import im.adamant.android.interactors.push.PushNotificationServiceFacade;

public class PushNotificationServiceHolder extends RecyclerView.ViewHolder {
    private TextView titleView;
    private TextView descriptionView;
    private Context context;

    public PushNotificationServiceHolder(@NonNull View itemView, Context context) {
        super(itemView);

        this.context = context;
        titleView = itemView.findViewById(R.id.list_item_push_notification_service_tv_title);
        descriptionView = itemView.findViewById(R.id.list_item_push_notification_service_tv_description);
    }

    public void bind(PushNotificationServiceFacade facade) {
        titleView.setText(context.getString(facade.getTitleResource()));
        descriptionView.setText(context.getString(facade.getDescriptionResource()));
    }
}
