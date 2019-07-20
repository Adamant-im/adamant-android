package im.adamant.android.ui.mvp_view;

import android.graphics.Bitmap;

import com.arellomobile.mvp.MvpView;

public interface ShowQrCodeView extends MvpView {
    void showQrCode(Bitmap bitmap);
    void showMessage(int resourceId);
    void showContent(String content);
}
