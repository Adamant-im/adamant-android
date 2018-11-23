package im.adamant.android.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import im.adamant.android.R;
import im.adamant.android.ui.mvp_view.ShowQrCodeView;
import im.adamant.android.ui.presenters.ShowQrCodePresenter;

public class ShowQrCodeScreen extends BaseActivity implements ShowQrCodeView {
    public static final String ARG_DATA_FOR_QR_CODE = "data";

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

        Intent intent = getIntent();
        if (intent != null){
            if (intent.hasExtra(ARG_DATA_FOR_QR_CODE)) {
                qrCodeView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        qrCodeView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int size = qrCodeView.getWidth();
                        presenter.onBuildQrCode(intent.getStringExtra(ARG_DATA_FOR_QR_CODE), size);
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
        presenter.onClickSaveQrCodeInGallery(this);
    }

    @Override
    public void showMessage(int resourceId) {
        Toast.makeText(this, resourceId, Toast.LENGTH_LONG).show();
    }
}
