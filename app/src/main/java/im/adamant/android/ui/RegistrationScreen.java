package im.adamant.android.ui;

import android.os.Bundle;

import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import dagger.android.AndroidInjection;
import im.adamant.android.R;
import im.adamant.android.ui.adapters.PassphraseAdapter;
import im.adamant.android.ui.transformations.PassphraseAvatarOutlineProvider;
import im.adamant.android.ui.transformations.PassphraseAvatarTransformation;

public class RegistrationScreen extends BaseActivity {

    @Inject
    PassphraseAdapter passphraseAdapter;

    @Inject
    PassphraseAvatarOutlineProvider outlineProvider;

    @Inject
    PassphraseAvatarTransformation avatarTransformation;

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
        viewPager.setOffscreenItems(3);
        viewPager.setOverScrollEnabled(false);

        viewPager.setItemTransformer(avatarTransformation);


    }
}
