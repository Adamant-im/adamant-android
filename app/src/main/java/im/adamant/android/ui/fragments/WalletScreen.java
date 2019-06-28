package im.adamant.android.ui.fragments;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.agrawalsuneet.loaderspack.loaders.ArcProgressLoader;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.google.android.material.tabs.TabLayout;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.OnClick;
import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.ui.ShowQrCodeScreen;
import im.adamant.android.ui.entities.CurrencyTransferEntity;
import im.adamant.android.helpers.QrCodeHelper;
import im.adamant.android.ui.fragments.base.BaseFragment;
import im.adamant.android.ui.presenters.WalletPresenter;
import im.adamant.android.ui.adapters.CurrencyCardAdapter;
import im.adamant.android.ui.adapters.CurrencyTransfersAdapter;
import im.adamant.android.ui.transformations.ShadowTransformation;
import im.adamant.android.ui.entities.CurrencyCardItem;
import im.adamant.android.ui.mvp_view.WalletView;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class WalletScreen extends BaseFragment implements WalletView {

    private Disposable cardEventsDisposable;

    @Inject
    @Named(Screens.WALLET_SCREEN)
    QrCodeHelper qrCodeHelper;

    @Inject
    Provider<WalletPresenter> presenterProvider;

    @Inject
    CurrencyCardAdapter currencyCardAdapter;

    @Inject
    CurrencyTransfersAdapter currencyTransfersAdapter;

    //--Moxy
    @InjectPresenter
    WalletPresenter presenter;

    @ProvidePresenter
    public WalletPresenter getPresenter(){
        return presenterProvider.get();
    }

    @BindView(R.id.fragment_wallet_tab_sliding_tabs) TabLayout tabs;
    @BindView(R.id.fragment_wallet_vp_swipe_slider) ViewPager slider;
    @BindView(R.id.fragment_wallet_rv_last_transactions) RecyclerView lastTransactions;
    @BindView(R.id.fragment_wallet_tv_last_transactions_title) TextView lastTransactionsTitle;
    @BindView(R.id.fragment_wallet_pb_transfer_loader) ArcProgressLoader transactionsLoader;

    private boolean isTabsNotRendered = true;

    public WalletScreen() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_wallet_screen;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ShadowTransformation transformer = new ShadowTransformation(slider, currencyCardAdapter);
        slider.setAdapter(currencyCardAdapter);
        slider.setPageTransformer(false, transformer);
        slider.setOffscreenPageLimit(3);

        slider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                CurrencyCardItem item = currencyCardAdapter.getItem(position);
                if (item != null){
                    String pattern = getString(R.string.fragment_wallet_last_transactions_title);
                    pattern = String.format(Locale.ENGLISH, pattern, item.getAbbreviation());
                    lastTransactionsTitle.setText(pattern);

                    presenter.onSelectCurrencyCard(item);
                }
            }

            @Override
            public void onPageSelected(int position) {
                TabLayout.Tab tab = tabs.getTabAt(position);

                if (tab != null) {
                    currencyCardAdapter.setSelectedIndex(position);
                    tab.select();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//        tabs.setupWithViewPager(slider);

        tabs.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int nextPosition = tab.getPosition();
                int currentPosition = slider.getCurrentItem();

                if (nextPosition != currentPosition) {
                    slider.setCurrentItem(nextPosition, true);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        lastTransactions.setLayoutManager(layoutManager);
        lastTransactions.setAdapter(currencyTransfersAdapter);

        Drawable divider = ContextCompat.getDrawable(lastTransactions.getContext(), R.drawable.line_divider);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(lastTransactions.getContext(), layoutManager.getOrientation());

        if (divider != null) {
            dividerItemDecoration.setDrawable(divider);
        }

        lastTransactions.addItemDecoration(dividerItemDecoration);

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        WeakReference<WalletPresenter> thisReference = new WeakReference<>(presenter);

        if (cardEventsDisposable != null) {
            cardEventsDisposable.dispose();
        }

        cardEventsDisposable = currencyCardAdapter
                .getObservable()
                .subscribe(event -> {
                    WalletPresenter presenter = thisReference.get();
                    if (presenter == null){return;}

                    switch (event){
                        case COPY:
                            presenter.onClickCopyCurrentCardAddress();
                            break;
                        case CREATE_QR:
                            presenter.onClickCreateQrCodeCurrentCardAddress();
                            break;
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onStopTransfersUpdate();
        cardEventsDisposable.dispose();
        cardEventsDisposable = null;
    }

    @OnClick(R.id.fragment_wallet_tv_see_all)
    public void onClickShowAllButton() {
        presenter.onClickShowAllTransfers();
    }

    @Override
    public void showCurrencyCards(List<CurrencyCardItem> currencyCardItems) {
        currencyCardAdapter.addCardItems(currencyCardItems);

        if (isTabsNotRendered) {
            renderTabs();
            isTabsNotRendered = false;
        }
    }

    @Override
    public void showLastTransfers(List<CurrencyTransferEntity> currencyTransferEntities) {
        currencyTransfersAdapter.refreshItems(currencyTransferEntities);
        transactionsLoader.setVisibility(View.GONE);
    }

    @Override
    public void startTransfersLoad() {
        transactionsLoader.setVisibility(View.VISIBLE);
    }

    @Override
    public void putAddressToClipboard(String address) {
        Activity activity = getActivity();
        if (activity != null){
            ClipData clip = ClipData.newPlainText("addressView", address);
            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);

            if(clipboard != null){
                clipboard.setPrimaryClip(clip);
                Toast.makeText(activity.getApplicationContext(), R.string.address_was_copied, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void createQrCode(String address) {
        Activity activity = getActivity();
        if (activity != null){
            Bundle bundle = new Bundle();
            bundle.putString(ShowQrCodeScreen.ARG_DATA_FOR_QR_CODE, address);
            bundle.putBoolean(ShowQrCodeScreen.ARG_SHOW_CONTENT, true);

            Intent intent = new Intent(activity.getApplicationContext(), ShowQrCodeScreen.class);
            intent.putExtras(bundle);

            startActivity(intent);
        }
    }

    private void renderTabs() {
        //Set Tabs
        for (int i = 0; i < currencyCardAdapter.getCount(); i++) {
            TabLayout.Tab tab = tabs.newTab();
            View tabCustomView = currencyCardAdapter.getTabCustomView(i, getLayoutInflater(),null);
            tab.setCustomView(tabCustomView);
            tabs.addTab(tab);
        }

        currencyCardAdapter.setSelectedIndex(0);
    }

}
