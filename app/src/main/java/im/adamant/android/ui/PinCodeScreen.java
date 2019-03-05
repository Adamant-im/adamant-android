package im.adamant.android.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import dagger.android.AndroidInjection;
import im.adamant.android.R;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.ui.mvp_view.PinCodeView;
import im.adamant.android.ui.presenters.PincodePresenter;
import io.reactivex.Flowable;

public class PinCodeScreen extends BaseActivity implements PinCodeView {
    public static final String ARG_MODE = "mode";

    @Inject
    Provider<PincodePresenter> presenterProvider;

    //--Moxy
    @InjectPresenter
    PincodePresenter presenter;

    @ProvidePresenter
    public PincodePresenter getPresenter(){
        return presenterProvider.get();
    }

    @BindView(R.id.activity_pincode_plv_keyboard)
    PinLockView pinLockView;
    @BindView(R.id.activity_pincode_id_indicator_dots) IndicatorDots indicatorDots;
    @BindView(R.id.activity_pincode_tv_suggestion)
    TextView suggestionView;

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            if (presenter != null){
                presenter.onInputPincodeWasCompleted(pin);
            }
        }

        @Override
        public void onEmpty() {
            Toast.makeText(getApplicationContext(), R.string.empty_pincode, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
            LoggerHelper.d("typing", "typing");
        }
    };

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

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        pinLockView.attachIndicatorDots(indicatorDots);
        pinLockView.setPinLockListener(mPinLockListener);


        //pinLockView.setCustomKeySet(new int[]{2, 3, 1, 5, 9, 6, 7, 0, 8, 4});
        //pinLockView.enableLayoutShuffling();

        pinLockView.setPinLength(4);

        indicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);

        Intent intent = getIntent();

        if (intent == null){return;}
        Bundle extras = intent.getExtras();

        if (extras == null){return;}
        MODE mode = (MODE) extras.getSerializable(ARG_MODE);

        if (mode == null){return;}
        presenter.setMode(mode);

    }

    @Override
    public void setSuggestion(int resourceId) {
        suggestionView.setText(resourceId);
    }

    @Override
    public void close(Bundle bundle) {
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void showError(int resourceId) {

    }
}
