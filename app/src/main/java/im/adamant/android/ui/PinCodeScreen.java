package im.adamant.android.ui;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import im.adamant.android.R;
import im.adamant.android.avatars.Avatar;
import io.reactivex.Flowable;

public class PinCodeScreen extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_pin_code_screen;
    }

    @Override
    public boolean withBackButton() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

}
