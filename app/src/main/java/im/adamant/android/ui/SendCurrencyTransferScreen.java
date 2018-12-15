package im.adamant.android.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import im.adamant.android.R;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import dagger.android.AndroidInjection;
import im.adamant.android.ui.adapters.SendCurrencyAdapter;
import im.adamant.android.ui.entities.SendCurrencyEntity;
import im.adamant.android.ui.mvp_view.SendCurrencyTransferView;
import im.adamant.android.ui.presenters.SendCurrencyPresenter;
import ru.terrakok.cicerone.NavigatorHolder;

public class SendCurrencyTransferScreen extends BaseActivity implements SendCurrencyTransferView {
    public static final String ARG_COMPANION_ID = "companion_id";

    @Inject
    NavigatorHolder navigatorHolder;

    @Inject
    Provider<SendCurrencyPresenter> presenterProvider;

    @Inject
    SendCurrencyAdapter adapter;

    //--Moxy
    @InjectPresenter
    SendCurrencyPresenter presenter;

    @ProvidePresenter
    public SendCurrencyPresenter getPresenter(){
        return presenterProvider.get();
    }

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
        if (intent != null){
            if (intent.hasExtra(ARG_COMPANION_ID)){
                String companionId = getIntent().getStringExtra(ARG_COMPANION_ID);
                presenter.onClickShowInterfaceFor(companionId);
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

    @Override
    public void showSendCurrencyInterface(List<SendCurrencyEntity> entityList) {
        if (adapter != null){
            adapter.setItems(entityList);
        }
    }
}
