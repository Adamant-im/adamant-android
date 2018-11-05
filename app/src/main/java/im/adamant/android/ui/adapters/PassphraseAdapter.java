package im.adamant.android.ui.adapters;

import android.content.Context;
import android.os.Build;
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
import im.adamant.android.ui.transformations.PassphraseAvatarOutlineProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class PassphraseAdapter extends RecyclerView.Adapter<PassphraseAdapter.PassphraseViewHolder> {
    private List<String> passphrases = new ArrayList<>();
    private List<String> publicKeys = new ArrayList<>();
    private Avatar avatar;
    private PassphraseAvatarOutlineProvider outlineProvider;
    private AdamantKeyGenerator keyGenerator;
    private CompositeDisposable subscriptions = new CompositeDisposable();

    public PassphraseAdapter(Avatar avatar, PassphraseAvatarOutlineProvider outlineProvider, AdamantKeyGenerator keyGenerator) {
        this.avatar = avatar;
        this.keyGenerator = keyGenerator;
        this.outlineProvider = outlineProvider;
    }

    public void setPassphrases(List<String> passphrases) {
        if (passphrases != null) {
            this.passphrases = passphrases;
            publicKeys.clear();
            for (String passphrase : passphrases) {
                KeyPair keyPair = keyGenerator.getKeyPairFromPassPhrase(passphrase);
                publicKeys.add(keyPair.getPublicKeyString().toLowerCase());
            }
        }
        notifyDataSetChanged();
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
                avatarView.setOutlineProvider(outlineProvider);
            }
        }

        public void bind(int position) {
            int size = (int) context.getResources().getDimension(R.dimen.list_item_passphrase_avatar_size);
            Disposable subscribe = avatar
                    .build(
                            publicKeys.get(position),
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
