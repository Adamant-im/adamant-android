package im.adamant.android.ui;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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
    @BindView(R.id.activity_scan_qrcode_no_permission_layout) TextView noPermissionView;

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

        if(TedPermission.isDenied(this, Manifest.permission.CAMERA)){
            scannerView.setVisibility(View.GONE);
            noPermissionView.setVisibility(View.VISIBLE);

            TedPermission.with(this)
                    .setPermissionListener(permissionlistener)
                    .setPermissions(Manifest.permission.CAMERA)
                    .check();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(TedPermission.isGranted(this, Manifest.permission.CAMERA)){
            scannerView.setVisibility(View.VISIBLE);
            noPermissionView.setVisibility(View.GONE);

            scannerView.setResultHandler(ScanQrCodeScreen.this); // Register ourselves as a handler for scan results.
            scannerView.startCamera();
        } else {
            scannerView.setVisibility(View.GONE);
            noPermissionView.setVisibility(View.VISIBLE);
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

            scannerView.setVisibility(View.VISIBLE);
            noPermissionView.setVisibility(View.GONE);
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            scannerView.setVisibility(View.GONE);
            noPermissionView.setVisibility(View.VISIBLE);
        }
    };
}
