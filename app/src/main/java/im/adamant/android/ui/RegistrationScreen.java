package im.adamant.android.ui;

import android.os.Build;
import android.os.Bundle;

import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.Pivot;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import dagger.android.AndroidInjection;
import im.adamant.android.R;
import im.adamant.android.ui.adapters.PassphraseAdapter;
import im.adamant.android.ui.adapters.ViewPagerPassphraseAdapter;
import im.adamant.android.ui.transformations.PassphraseAvatarTransformation;

public class RegistrationScreen extends BaseActivity {

    @Inject
    PassphraseAdapter passphraseAdapter;

//    @BindView(R.id.activity_registration_vp_carousel)
//    ViewPager viewPager;

    @BindView(R.id.activity_registration_vp_carousel)
    DiscreteScrollView viewPager;

    @Override
    public int getLayoutId() {
        return R.layout.activity_registration_screen;
    }

    @Override
    public boolean withBackButton() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        List<String> passphrases = new ArrayList<>();
        passphrases.add("panther soul into disorder quiz worth general travel gun imitate dumb square");
        passphrases.add("next ladder cost spell dove suffer resemble fragile agent hamster forest angry");
        passphrases.add("vacuum victory fatal priority inspire will blouse family shop danger accuse talent");

        passphraseAdapter.setPassphrases(passphrases);

        viewPager.setAdapter(passphraseAdapter);
        viewPager.setOffscreenItems(3); //Reserve extra space equal to (childSize * count) on each side of the view
        viewPager.setOverScrollEnabled(false);

        PassphraseAvatarTransformation passphraseAvatarTransformation = new PassphraseAvatarTransformation();
        viewPager.setItemTransformer(passphraseAvatarTransformation);

//        viewPager.setItemTransformer(new ScaleTransformer.Builder()
//                .setMaxScale(1.05f)
//                .setMinScale(0.8f)
//                .setPivotX(Pivot.X.CENTER) // CENTER is a default one
//                .setPivotY(Pivot.Y.BOTTOM) // CENTER is a default one
//                .build());


//        viewPager.setPagingEnabled(true);
//        viewPager.setAdapter(passphraseAdapter);
//        viewPager.setOffscreenPageLimit(3);
    }
}
