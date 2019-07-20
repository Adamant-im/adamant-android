package im.adamant.android.ui.presenters;

import android.content.Context;
import android.widget.Toast;

import com.arellomobile.mvp.InjectViewState;
import com.gun0912.tedpermission.PermissionListener;

import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.image.ImageType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import im.adamant.android.R;
import im.adamant.android.helpers.QrCodeHelper;
import im.adamant.android.ui.RegistrationScreen;
import im.adamant.android.ui.ShowQrCodeScreen;
import im.adamant.android.ui.mvp_view.ShowQrCodeView;
import io.reactivex.disposables.CompositeDisposable;

@InjectViewState
public class ShowQrCodePresenter extends BasePresenter<ShowQrCodeView> {
    private QrCodeHelper qrCodeHelper;

    private QRCode qrCode;

    public ShowQrCodePresenter(QrCodeHelper qrCodeHelper) {
        this.qrCodeHelper = qrCodeHelper;
    }

    public void onBuildQrCode(String data, int sizePixel, int onColor, int backgroundColor, boolean isShowContent) {
        qrCode = QRCode.from(data)
                .to(ImageType.PNG)
                .withSize(sizePixel, sizePixel)
                .withColor(onColor, backgroundColor);
        getViewState().showQrCode(qrCode.bitmap());

        if (isShowContent) {
            getViewState().showContent(data);
        }
    }

    public void onClickSaveQrCodeInGallery(Context context) {
        if (qrCode == null) { return; }

        File qrCodeFile = qrCodeHelper.makeImageFile("pass_");
        try (OutputStream stream = new FileOutputStream(qrCodeFile)) {
            qrCode.writeTo(stream);
            qrCodeHelper.registerImageInGallery(context, qrCodeFile);
            getViewState().showMessage(R.string.qrcode_was_created_in_gallery);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
