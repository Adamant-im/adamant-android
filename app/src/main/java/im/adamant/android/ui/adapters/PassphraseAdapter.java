package im.adamant.android.ui.adapters;

import android.content.Context;
import android.os.Build;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.goterl.lazycode.lazysodium.utils.KeyPair;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import im.adamant.android.R;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.core.encryption.AdamantKeyGenerator;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.rx.ObservableRxList;
import im.adamant.android.ui.transformations.PassphraseAvatarOutlineProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

public class PassphraseAdapter extends RecyclerView.Adapter<PassphraseAdapter.PassphraseViewHolder> {
    private List<Pair<String, String>> passphrases = new ArrayList<>();
    private Avatar avatar;
    private PassphraseAvatarOutlineProvider outlineProvider;
    private CompositeDisposable subscriptions = new CompositeDisposable();
    private final PublishSubject<Integer> clickItemPublisher = PublishSubject.create();

    public PassphraseAdapter(Avatar avatar) {
        this.avatar = avatar;
    }

    public void setOutlineProvider(PassphraseAvatarOutlineProvider outlineProvider) {
        this.outlineProvider = outlineProvider;
    }

    public void setPassphrases(List<Pair<String, String>> passphrases) {
        if (passphrases != null) {
            this.passphrases = passphrases;
        }
        notifyDataSetChanged();
    }

    public Observable<Integer> getObservable() {
        return clickItemPublisher;
    }

    @Override
    protected void finalize() throws Throwable {
        subscriptions.dispose();
        subscriptions.clear();
        super.finalize();
    }

    @NonNull
    @Override
    public PassphraseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_passphrase, parent, false);
        return new PassphraseViewHolder(parent.getContext(), outlineProvider, v);
    }

    @Override
    public void onBindViewHolder(@NonNull PassphraseViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return passphrases.size();
    }


    public class PassphraseViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatarView;
        private Context context;

        public PassphraseViewHolder(Context context, PassphraseAvatarOutlineProvider outlineProvider, @NonNull View itemView) {
            super(itemView);

            this.context = context;

            avatarView = itemView.findViewById(R.id.list_item_passphrase_avatar);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (outlineProvider != null) {
                    avatarView.setOutlineProvider(outlineProvider);
                }
            }

            avatarView.setOnClickListener((v) -> {
                if (v.getTag() instanceof Integer){
                    Integer index = (Integer) v.getTag();
                    clickItemPublisher.onNext(index);
                }
            });
        }

        public void bind(int position) {
            int size = (int) context.getResources().getDimension(R.dimen.list_item_passphrase_avatar_size);
            Pair<String, String> pair = passphrases.get(position);

            avatarView.setTag(position);

            if (pair.second == null || pair.second.isEmpty()){
                avatarView.setImageResource(R.mipmap.ic_launcher_round);
            } else {
                Disposable subscribe = avatar
                        .build(
                                pair.second,
                                size
                        )
                        .subscribe(
                                image -> avatarView.setImageBitmap(image),
                                Throwable::printStackTrace
                        );
                subscriptions.add(subscribe);
            }
        }
    }
}
