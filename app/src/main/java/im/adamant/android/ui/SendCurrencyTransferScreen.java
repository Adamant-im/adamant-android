package im.adamant.android.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import im.adamant.android.R;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import dagger.android.AndroidInjection;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.ui.adapters.SendCurrencyAdapter;
import im.adamant.android.ui.adapters.SendCurrencyFragmentAdapter;
import im.adamant.android.ui.entities.SendCurrencyEntity;
import im.adamant.android.ui.mvp_view.SendCurrencyTransferView;
import im.adamant.android.ui.presenters.SendCurrencyPresenter;
import io.reactivex.Flowable;
import ru.terrakok.cicerone.NavigatorHolder;

public class SendCurrencyTransferScreen extends BaseActivity implements HasSupportFragmentInjector {
    public static final String ARG_COMPANION_ID = "companion_id";

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    @Inject
    NavigatorHolder navigatorHolder;

//    @Inject
//    Provider<SendCurrencyPresenter> presenterProvider;
//
    @Inject
    SendCurrencyFragmentAdapter adapter;

    @BindView(R.id.activity_currency_transfer_tab_currencies) TabLayout tabs;
    @BindView(R.id.activity_currency_transfer_vp_swipe_slider) ViewPager slider;

    @Override
    public int getLayoutId() {
        return R.layout.activity_currency_transfer_screen;
    }

    @Override
    public boolean withBackButton() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(ARG_COMPANION_ID)) {
                String companionId = getIntent().getStringExtra(ARG_COMPANION_ID);
                adapter.setCompanionId(companionId);
            }
        }

        slider.setAdapter(adapter);

        slider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                CurrencyCardItem item = currencyCardAdapter.getItem(position);
//                if (item != null){
//                    presenter.onSelectCurrencyCard(item);
//                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabs.setupWithViewPager(slider);


    }

//    @Override
//    public void showSendCurrencyInterface(List<SendCurrencyEntity> entityList) {
//        if (adapter != null){
//            adapter.setItems(entityList);
//        }
//    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }
}
