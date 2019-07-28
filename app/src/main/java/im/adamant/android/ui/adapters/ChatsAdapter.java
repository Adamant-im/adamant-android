package im.adamant.android.ui.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import im.adamant.android.R;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.rx.AbstractObservableRxList;
import im.adamant.android.rx.ThreadUnsafeObservableRxList;
import im.adamant.android.ui.entities.Chat;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import com.github.curioustechizen.ago.RelativeTimeTextView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder>{
    private AbstractObservableRxList<Chat> chats = new ThreadUnsafeObservableRxList<>();
    private SelectItemListener listener;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Avatar avatar;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public Context context;

        private TextView chatView;
        private TextView lastMessageView;
        private RelativeTimeTextView dateView;
        private ImageView avatarView;
        private Avatar avatar;

        int avatarSize;

        public ViewHolder(Context context, View v, Avatar avatar) {
            super(v);

            this.context = context;
            this.avatar = avatar;

            this.chatView = v.findViewById(R.id.list_item_chat_name);
            this.lastMessageView = v.findViewById(R.id.list_item_chat_last_message);
            this.dateView = v.findViewById(R.id.list_item_chat_last_message_date);
            this.avatarView = v.findViewById(R.id.list_item_chat_avatar);

            v.setOnClickListener(this);

           avatarSize = (int) context.getResources().getDimension(R.dimen.list_item_avatar_size);

        }

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            if((listener != null) && (position >= 0)) {
                listener.itemWasSelected(chats.get(position));
            }
        }

        public void bind(Chat chat) {
            chatView.setText(chat.getTitle());
            if (chat.getLastMessage() != null){
                lastMessageView.setText(chat.getLastMessage().getShortedMessage(context, 25));
                dateView.setReferenceTime(chat.getLastMessage().getTimestamp());
            } else {
                lastMessageView.setText("");
                dateView.setReferenceTime(System.currentTimeMillis());
            }

            avatarView.setImageBitmap(null);
            if (chat.getCompanionPublicKey() != null){
                Disposable avatarSubscription = avatar
                        .build(chat.getCompanionPublicKey(), avatarSize)
                        .subscribe(
                                avatar -> avatarView.setImageBitmap(avatar),
                                error -> LoggerHelper.e("chatHolder", error.getMessage(), error)
                        );
                compositeDisposable.add(avatarSubscription);
            } else {
                avatarView.setImageResource(R.mipmap.ic_launcher_foreground);
            }
        }
    }

    public interface SelectItemListener {
        void itemWasSelected(Chat chat);
    }

    public ChatsAdapter(
            AbstractObservableRxList<Chat> chats,
            SelectItemListener listener,
            Avatar avatar
    ) {
        this.avatar = avatar;

        if (chats != null){
            this.chats = chats;
            Disposable disposable = chats
                    .getEventObservable()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            event -> notifyDataSetChanged(),
                            error -> LoggerHelper.e(getClass().getSimpleName(), error.getMessage(), error)
                    );
            compositeDisposable.add(disposable);
        }

        if (listener != null){
            this.listener = listener;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_chat, parent, false);

        return new ViewHolder(parent.getContext(), v, avatar);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Chat chat = chats.get(position);
        if (chat != null){
            holder.bind(chat);
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public void setListener(SelectItemListener listener) {
        this.listener = listener;
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
