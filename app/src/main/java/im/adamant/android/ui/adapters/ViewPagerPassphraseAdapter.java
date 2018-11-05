package im.adamant.android.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.goterl.lazycode.lazysodium.utils.KeyPair;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import im.adamant.android.R;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.core.encryption.AdamantKeyGenerator;
import im.adamant.android.helpers.LoggerHelper;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class ViewPagerPassphraseAdapter extends PagerAdapter {
    private List<String> passphrases = new ArrayList<>();
    private List<String> publicKeys = new ArrayList<>();
    private Avatar avatar;
    private AdamantKeyGenerator keyGenerator;
    private CompositeDisposable subscriptions = new CompositeDisposable();

    public ViewPagerPassphraseAdapter(Avatar avatar, AdamantKeyGenerator keyGenerator) {
        this.avatar = avatar;
        this.keyGenerator = keyGenerator;
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
    public int getCount() {
        return passphrases.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = LayoutInflater.from(container.getContext())
                    .inflate(R.layout.list_item_passphrase, container, false);
            container.addView(view);
            bind(container.getContext(), position, view);
//            ImageView avatar = (ImageView) view.findViewById(R.id.list_item_passphrase_avatar);

//            if (mBaseElevation == 0) {
//                mBaseElevation = cardView.getCardElevation();
//            }
//
//            cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
//            if (position < views.size()){
//                views.set(position, cardView);
//            } else {
//                views.add(cardView);
//            }
            return view;
    }

    @Override
    public void destroyItem(@NonNull View container, int position, @NonNull Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    public void bind(Context context, int position, View view) {
        ImageView avatarView = view.findViewById(R.id.list_item_passphrase_avatar);
        int size = (int) context.getResources().getDimension(R.dimen.list_item_passphrase_avatar_size);
        Disposable avatarSubscription = avatar
                .build(publicKeys.get(position), size)
                .subscribe(
                        avatarView::setImageBitmap,
                        error -> LoggerHelper.e("Passphrase", error.getMessage(), error)
                );
        subscriptions.add(avatarSubscription);
    }

    @Override
    protected void finalize() throws Throwable {
        subscriptions.dispose();
        subscriptions.clear();
        super.finalize();
    }
}
