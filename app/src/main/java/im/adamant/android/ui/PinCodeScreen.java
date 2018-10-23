package im.adamant.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import dagger.Lazy;
import dagger.android.AndroidInjection;
import im.adamant.android.Constants;
import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.presenters.PincodePresenter;
import im.adamant.android.ui.mvp_view.PincodeView;
import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.commands.Command;
import ru.terrakok.cicerone.commands.Forward;
import ru.terrakok.cicerone.commands.SystemMessage;

public class PinCodeScreen extends BaseActivity implements PincodeView {
    public static final String ARG_MODE = "pincode_mode";

    @Inject
    NavigatorHolder navigatorHolder;

    @Inject
    Provider<PincodePresenter> presenterProvider;

    //--Moxy
    @InjectPresenter
    PincodePresenter presenter;

    @ProvidePresenter
    public PincodePresenter getPresenter(){
        return presenterProvider.get();
    }

    @BindView(R.id.activity_pincode_plv_keyboard) PinLockView pinLockView;
    @BindView(R.id.activity_pincode_id_indicator_dots) IndicatorDots indicatorDots;
    @BindView(R.id.activity_pincode_tv_suggestion) TextView suggestionView;

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            if (presenter != null){
                presenter.onEnteredPincode(pin);
            }
        }

        @Override
        public void onEmpty() {
            Toast.makeText(getApplicationContext(), R.string.activity_pincode_empty_pincode, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {

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
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        pinLockView.attachIndicatorDots(indicatorDots);
        pinLockView.setPinLockListener(mPinLockListener);
        pinLockView.setPinLength(10);

        indicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(ARG_MODE)) {
                PincodePresenter.Mode mode = (PincodePresenter.Mode) getIntent().getSerializableExtra(ARG_MODE);
                presenter.setCurrentMode(mode);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigatorHolder.setNavigator(navigator);
    }

    @Override
    protected void onPause() {
        super.onPause();
        navigatorHolder.removeNavigator();
    }

    @Override
    public void setSuggestionResource(int resourceId) {
        suggestionView.setText(getString(resourceId));
    }

    @Override
    public void close() {
        finish();
    }

    @Override
    public void showMessage(int stringResourceId) {
        Toast.makeText(this, stringResourceId, Toast.LENGTH_LONG).show();
    }

    private Navigator navigator = new Navigator() {
        @Override
        public void applyCommands(Command[] commands) {
            for (Command command : commands){
                apply(command);
            }
        }

        private void apply(Command command){
            if (command instanceof Forward) {
                Forward forward = (Forward)command;
                switch (forward.getScreenKey()){
                    case Screens.CHATS_SCREEN: {
                        Bundle bundle = new Bundle();
                        bundle.putString(MainScreen.ARG_CURRENT_SCREEN, Screens.CHATS_SCREEN);

                        Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                        intent.putExtras(bundle);

                        startActivity(intent);
                        finish();
                    }
                    break;

                    case Screens.LOGIN_SCREEN: {
                        Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
                        startActivity(intent);
                        finish();
                    }
                    break;

                }
            } else if(command instanceof SystemMessage){
                SystemMessage message = (SystemMessage) command;
                Toast.makeText(getApplicationContext(), message.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    };
}
