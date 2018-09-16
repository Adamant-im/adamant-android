package im.adamant.android.ui.holders;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Locale;

import im.adamant.android.R;
import im.adamant.android.core.entities.ServerNode;
import io.reactivex.subjects.PublishSubject;

public class ServerNodeHolder extends RecyclerView.ViewHolder {
    private Context context;
    private PublishSubject<Integer> subject;

    private TextView serverNameView;
    private TextView serverStatusView;
    private ImageButton deleteButton;


    public ServerNodeHolder(View itemView, Context context, PublishSubject<Integer> subject) {
        super(itemView);

        this.context = context;

        serverNameView = itemView.findViewById(R.id.list_item_servernode_tv_name);
        serverStatusView = itemView.findViewById(R.id.list_item_servernode_tv_status);
        deleteButton = itemView.findViewById(R.id.list_item_servernode_ibtn_delete);

        deleteButton.setOnClickListener(v -> subject.onNext(getAdapterPosition()));
    }

    public void bind(ServerNode serverNode){
        if (serverNode != null){
            serverNameView.setText(serverNode.getUrl());
            serverStatusView.setTextColor(ContextCompat.getColor(context, detectStatusColor(serverNode)));
            String statusString = String.format(
                    Locale.ENGLISH,
                    context.getString(R.string.fragment_settings_node_status),
                    detectStatus(serverNode),
                    serverNode.getPingInMilliseconds()
            );

            serverStatusView.setText(statusString);
        }
    }

    private int detectStatusColor(ServerNode serverNode) {
        switch (serverNode.getStatus()){
            case ACTIVE:
                return R.color.status_active;
            case CONNECTED:
                return R.color.status_connected;
            case CONNECTING:
                return R.color.status_connecting;
            case UNAVAILABLE:
                return R.color.status_unavailable;
            default:
                return R.color.status_unavailable;
        }
    }

    private String detectStatus(ServerNode serverNode) {
        switch (serverNode.getStatus()){
            case ACTIVE:
                return context.getString(R.string.node_status_active);
            case CONNECTED:
                return context.getString(R.string.node_status_connected);
            case CONNECTING:
                return context.getString(R.string.node_status_connecting);
            case UNAVAILABLE:
                return context.getString(R.string.node_status_unavailable);
            default:
                return context.getString(R.string.node_status_unavailable);
        }
    }
}
