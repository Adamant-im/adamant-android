package im.adamant.android.ui.presenters;

import android.content.Context;
import android.widget.Toast;

import com.arellomobile.mvp.InjectViewState;
import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.image.ImageType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import im.adamant.android.R;
import im.adamant.android.helpers.QrCodeHelper;
import im.adamant.android.ui.RegistrationScreen;
import im.adamant.android.ui.mvp_view.ShowQrCodeView;
import io.reactivex.disposables.CompositeDisposable;

@InjectViewState
public class ShowQrCodePresenter extends BasePresenter<ShowQrCodeView> {
    private QrCodeHelper qrCodeHelper;

    private QRCode qrCode;

    public ShowQrCodePresenter(QrCodeHelper qrCodeHelper, CompositeDisposable subscriptions) {
        super(subscriptions);
        this.qrCodeHelper = qrCodeHelper;
    }

    public void onBuildQrCode(String data, int sizePixel) {
        qrCode = QRCode.from(data).to(ImageType.PNG).withSize(sizePixel, sizePixel);
        getViewState().showQrCode(qrCode.bitmap());
    }

    public void onClickSaveQrCodeInGallery(Context context) {
        if (qrCode == null) { return; }

        File qrCodeFile = qrCodeHelper.makeImageFile("pass_");
        try (OutputStream stream = new FileOutputStream(qrCodeFile)) {
            qrCode.writeTo(stream);
            qrCodeHelper.registerImageInGallery(context, qrCodeFile);
            getViewState().showMessage(R.string.passphrase_qrcode_was_created);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
