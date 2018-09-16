package im.adamant.android.ui.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.image.ImageType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import butterknife.BindView;
import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.helpers.QrCodeHelper;
import im.adamant.android.presenters.WalletPresenter;
import im.adamant.android.ui.mvp_view.WalletView;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class WalletScreen extends BaseFragment implements WalletView {

    @Inject
    @Named(Screens.WALLET_SCREEN)
    QrCodeHelper qrCodeHelper;

    @Inject
    Provider<WalletPresenter> presenterProvider;

    //--Moxy
    @InjectPresenter
    WalletPresenter presenter;

    @ProvidePresenter
    public WalletPresenter getPresenter(){
        return presenterProvider.get();
    }

    @BindView(R.id.fragment_wallet_tv_adamant_id) TextView adamantAddressView;
    @BindView(R.id.fragment_wallet_tv_adamant_balance) TextView adamantBalanceView;
    @BindView(R.id.fragment_wallet_cl_free_tokens) View freeTokenButton;
    @BindView(R.id.fragment_wallet_cl_adamant_id) View copyAdamantAddressButton;
    @BindView(R.id.fragment_wallet_cl_exit) View exitButton;
    @BindView(R.id.fragment_wallet_cl_create_qr_code) View createQrCodeButton;

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

        freeTokenButton.setOnClickListener((v) -> {
            presenter.onClickGetFreeTokenButton();
        });

        //Do not use the presenter in order to avoid duplication of operations when switching fragments.
        copyAdamantAddressButton.setOnClickListener((v) -> {
            Activity activity = getActivity();
            if (activity != null){
                ClipData clip = ClipData.newPlainText("address", adamantAddressView.getText().toString());
                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);

                if(clipboard != null){
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(activity.getApplicationContext(), R.string.address_was_copied, Toast.LENGTH_LONG).show();
                }
            }
        });

        exitButton.setOnClickListener((v) -> {
            //VERY IMPORTANT: Do not delete the lock code of the button as this will result in a memory leak and crash the application.
            exitButton.setEnabled(false);
            Activity activity = getActivity();
            if (activity != null){
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder
                        .setTitle(R.string.dialog_logout_title)
                        .setMessage(R.string.dialog_logout_message)
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            presenter.onClickExitButton();
                            exitButton.setEnabled(true);
                        })
                        .setNegativeButton(android.R.string.cancel, (dialog, which) -> exitButton.setEnabled(true))
                        .setOnDismissListener(dialogInterface -> exitButton.setEnabled(true))
                        .show();
            }
        });

        createQrCodeButton.setOnClickListener((v) -> {
            Activity activity = getActivity();
            if (activity != null){
                TedPermission.with(activity)
                        .setRationaleMessage(R.string.rationale_qrcode_write_permission)
                        .setPermissionListener(permissionlistener)
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();
            }
        });

        return view;
    }

    @Override
    public void displayAdamantAddress(String address) {
        adamantAddressView.setText(address);
    }

    @Override
    public void displayAdamantBalance(BigDecimal balance) {
        adamantBalanceView.setText(String.format(Locale.ENGLISH, "%.3f", balance));
    }

    @Override
    public void displayFreeTokenPageButton() {
        freeTokenButton.setVisibility(View.VISIBLE);
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Activity activity = getActivity();
            if (activity != null){
                File qrCodeFile = qrCodeHelper.makeImageFile("address_");
                try (OutputStream stream = new FileOutputStream(qrCodeFile)){
                    QRCode.from("adm:" + adamantAddressView.getText().toString()).to(ImageType.PNG).writeTo(stream);
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

}
