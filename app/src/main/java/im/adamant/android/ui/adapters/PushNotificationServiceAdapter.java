package im.adamant.android.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import im.adamant.android.R;
import im.adamant.android.interactors.push.PushNotificationServiceFacade;
import im.adamant.android.ui.holders.PushNotificationServiceHolder;

public class PushNotificationServiceAdapter extends RecyclerView.Adapter<PushNotificationServiceHolder> {

    private List<PushNotificationServiceFacade> facadeList = new ArrayList<>();

    public PushNotificationServiceAdapter(List<PushNotificationServiceFacade> facadeList) {
        if (facadeList != null) {
            this.facadeList = facadeList;
        }
    }

    @NonNull
    @Override
    public PushNotificationServiceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item_push_notification_service, parent, false);
        return new PushNotificationServiceHolder(v, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull PushNotificationServiceHolder holder, int position) {
        holder.bind(facadeList.get(position));
    }

    @Override
    public int getItemCount() {
        return facadeList.size();
    }
}
