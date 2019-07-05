package im.adamant.android.ui;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Provider;

import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import im.adamant.android.Constants;
import im.adamant.android.R;
import im.adamant.android.ui.mvp_view.ShowQrCodeView;
import im.adamant.android.ui.presenters.ShowQrCodePresenter;

public class ShowQrCodeScreen extends BaseActivity implements ShowQrCodeView {
    public static final String ARG_DATA_FOR_QR_CODE = "data";
    public static final String ARG_SHOW_CONTENT = "show_content";
    public static final String ARG_TITLE_RESOURCE = "title";
    @Inject
    Provider<ShowQrCodePresenter> presenterProvider;

    //--Moxy
    @InjectPresenter
    ShowQrCodePresenter presenter;

    @ProvidePresenter
    public ShowQrCodePresenter getPresenter(){
        return presenterProvider.get();
    }

    @BindView(R.id.activity_show_qr_code_im_image)
    ImageView qrCodeView;

    @BindView(R.id.activity_show_qr_code_tv_content)
    TextView contentView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_show_qr_code_screen;
    }

    @Override
    public boolean withBackButton() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        //setTitle(getString(R.string.activity_show_qr_code_title));

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(ARG_TITLE_RESOURCE)) {
                setTitle(getString(intent.getIntExtra(ARG_TITLE_RESOURCE,0)));
            }

            if (intent.hasExtra(ARG_DATA_FOR_QR_CODE)) {
                final int backgroundColor = ContextCompat.getColor(this, R.color.alternate_background);
                final int onColor = ContextCompat.getColor(this, R.color.onPrimary);

                qrCodeView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        qrCodeView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int size = qrCodeView.getWidth();

                        boolean isShowContent = intent.getBooleanExtra(ARG_SHOW_CONTENT, false);
                        presenter.onBuildQrCode(
                                intent.getStringExtra(ARG_DATA_FOR_QR_CODE),
                                size,
                                onColor,
                                backgroundColor,
                                isShowContent
                        );
                    }
                });
            }
        }
    }

    @Override
    public void showQrCode(Bitmap bitmap) {
        qrCodeView.setImageBitmap(bitmap);
    }


    @OnClick(R.id.activity_show_qr_code_btn_save_in_gallery)
    public void onClickSaveInGalleryButton() {
        if (TedPermission.isGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            presenter.onClickSaveQrCodeInGallery(this);
        } else {
            TedPermission.with(this)
                .setRationaleMessage(R.string.rationale_qrcode_write_permission)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
        }
    }

    @Override
    public void showMessage(int resourceId) {
        Toast.makeText(this, resourceId, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showContent(String content) {
        contentView.setText(content);
    }

    private PermissionListener permissionlistener = new PermissionListener() {
        WeakReference<ShowQrCodeScreen> weakReference = new WeakReference<>(ShowQrCodeScreen.this);
        @Override
        public void onPermissionGranted() {
            ShowQrCodeScreen activity = weakReference.get();
            if (activity != null) {
                activity.presenter.onClickSaveQrCodeInGallery(activity);
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {

        }
    };
}
