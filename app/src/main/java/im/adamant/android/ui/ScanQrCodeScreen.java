package im.adamant.android.ui;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.zxing.Result;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import butterknife.BindView;
import dagger.android.AndroidInjection;
import im.adamant.android.R;
import im.adamant.android.ui.mvp_view.ScanQrCodeView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanQrCodeScreen extends BaseActivity implements ScanQrCodeView, ZXingScannerView.ResultHandler {

    @BindView(R.id.activity_scan_qrcode_zxscv_scanner) ZXingScannerView scannerView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_scan_qrcode_screen;
    }

    @Override
    public boolean withBackButton() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(TedPermission.isDenied(this, Manifest.permission.CAMERA)){
            TedPermission.with(this)
                    .setPermissionListener(permissionlistener)
                    .setPermissions(Manifest.permission.CAMERA)
                    .check();
        } else {
            scannerView.setResultHandler(ScanQrCodeScreen.this); // Register ourselves as a handler for scan results.
            scannerView.startCamera();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        Intent data = new Intent();
        data.setData(Uri.parse(result.getText()));

        setResult(RESULT_OK, data);
        finish();
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            scannerView.setResultHandler(ScanQrCodeScreen.this); // Register ourselves as a handler for scan results.
            scannerView.startCamera();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {

        }
    };
}
