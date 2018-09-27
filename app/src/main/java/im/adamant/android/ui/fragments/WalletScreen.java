package im.adamant.android.ui.fragments;


import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.gun0912.tedpermission.PermissionListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.currencies.CurrencyTransferEntity;
import im.adamant.android.helpers.QrCodeHelper;
import im.adamant.android.presenters.WalletPresenter;
import im.adamant.android.ui.adapters.CurrencyCardAdapter;
import im.adamant.android.ui.adapters.CurrencyTransfersAdapter;
import im.adamant.android.ui.custom_view.ShadowTransformer;
import im.adamant.android.ui.entities.CurrencyCardItem;
import im.adamant.android.ui.mvp_view.WalletView;

/**
 * A simple {@link Fragment} subclass.
 */
public class WalletScreen extends BaseFragment implements WalletView {

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

    @BindView(R.id.fragment_wallet_vp_swipe_slider)
    ViewPager slider;

    @BindView(R.id.fragment_wallet_rv_last_transactions)
    RecyclerView lastTransactions;

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
        ShadowTransformer transformer = new ShadowTransformer(slider, currencyCardAdapter);
        slider.setAdapter(currencyCardAdapter);
        slider.setPageTransformer(false, transformer);
        slider.setOffscreenPageLimit(3);

        slider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                CurrencyCardItem item = currencyCardAdapter.getItem(position);
                if (item != null){
                    presenter.onSelectCurrencyCard(item);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        lastTransactions.setLayoutManager(layoutManager);
        lastTransactions.setAdapter(currencyTransfersAdapter);

//        //Do not use the presenter in order to avoid duplication of operations when switching fragments.
//        copyAdamantAddressButton.setOnClickListener((v) -> {
//            Activity activity = getActivity();
//            if (activity != null){
//                ClipData clip = ClipData.newPlainText("addressView", adamantAddressView.getText().toString());
//                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
//
//                if(clipboard != null){
//                    clipboard.setPrimaryClip(clip);
//                    Toast.makeText(activity.getApplicationContext(), R.string.address_was_copied, Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//

//
//        createQrCodeButton.setOnClickListener((v) -> {
//            Activity activity = getActivity();
//            if (activity != null){
//                TedPermission.with(activity)
//                        .setRationaleMessage(R.string.rationale_qrcode_write_permission)
//                        .setPermissionListener(permissionlistener)
//                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        .check();
//            }
//        });

        return view;
    }


    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Activity activity = getActivity();
            if (activity != null){
                File qrCodeFile = qrCodeHelper.makeImageFile("address_");
                try (OutputStream stream = new FileOutputStream(qrCodeFile)){
//                    QRCode.from("adm:" + adamantAddressView.getText().toString()).to(ImageType.PNG).writeTo(stream);
                    qrCodeHelper.registerImageInGallery(activity, qrCodeFile);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {

        }
    };

    @Override
    public void showCurrencyCards(List<CurrencyCardItem> currencyCardItems) {
        currencyCardAdapter.addCardItems(currencyCardItems);
    }

    @Override
    public void showLastTransfers(List<CurrencyTransferEntity> currencyTransferEntities) {
        currencyTransfersAdapter.refreshItems(currencyTransferEntities);
    }
}
