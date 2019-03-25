package im.adamant.android.ui;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.concurrent.TimeUnit;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import im.adamant.android.Constants;
import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.interactors.AuthorizeInteractor;
import im.adamant.android.ui.adapters.WelcomeCardsAdapter;
import im.adamant.android.ui.fragments.BottomLoginFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import im.adamant.android.ui.transformations.SimpleDotIndicatorDecoration;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.commands.Command;
import ru.terrakok.cicerone.commands.Forward;
import ru.terrakok.cicerone.commands.SystemMessage;


public class LoginScreen extends BaseActivity implements  HasSupportFragmentInjector {

    @Inject
    WelcomeCardsAdapter adapter;

    @Inject
    NavigatorHolder navigatorHolder;

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    @Inject
    AuthorizeInteractor authorizeInteractor;

    @BindView(R.id.activity_login_vp_welcome_cards) DiscreteScrollView welcomeCardsSliderView;
    @BindView(R.id.activity_login_btn_generate_new_passphrase) MaterialButton creteNewButtonView;

    private BottomLoginFragment loginFragment;

    //--Activity
    @Override
    public int getLayoutId() {
        return R.layout.activity_login_screen;
    }

    @Override
    public boolean withBackButton() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        if (authorizeInteractor.isAuthorized()) {
            navigator.applyCommands(new Command[]{
                    new Forward(Screens.WALLET_SCREEN, null)
            });
        }
        super.onCreate(savedInstanceState);

        if (loginFragment == null) {
            loginFragment = new BottomLoginFragment();
        }

        welcomeCardsSliderView.setAdapter(adapter);
        welcomeCardsSliderView.setOffscreenItems(1);
        welcomeCardsSliderView.setOverScrollEnabled(true);
        welcomeCardsSliderView.addItemDecoration(
                new SimpleDotIndicatorDecoration(
                        ContextCompat.getColor(this, R.color.secondaryDarkVariant),
                        ContextCompat.getColor(this, R.color.secondaryLightVariant),
                        20
                )
        );

        creteNewButtonView.setPaintFlags(creteNewButtonView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    @OnClick(R.id.activity_login_ib_node_list)
    public void showNodesList() {
        Intent intent = new Intent(getApplicationContext(), NodesListScreen.class);
        startActivity(intent);
    }

    @OnClick(R.id.activity_login_btn_login)
    public void loginButtonClick() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        Fragment fragment = supportFragmentManager.findFragmentByTag(loginFragment.getTag());
        if (fragment == null) {
            loginFragment.show(supportFragmentManager, loginFragment.getTag());
        }
    }

    @OnClick(R.id.activity_login_btn_generate_new_passphrase)
    public void generateNewPassphraseClick() {
        Intent intent = new Intent(getApplicationContext(), RegistrationScreen.class);
        startActivity(intent);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (loginFragment != null){
            loginFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
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
                    case Screens.SETTINGS_SCREEN:
                    case Screens.WALLET_SCREEN:
                    case Screens.CHATS_SCREEN: {
                        Bundle bundle = new Bundle();
                        bundle.putString(MainScreen.ARG_CURRENT_SCREEN, forward.getScreenKey());

                        Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                        intent.putExtras(bundle);

                        startActivity(intent);
                        finish();
                    }
                    break;
                    case Screens.SCAN_QRCODE_SCREEN: {
                        Intent intent = new Intent(getApplicationContext(), ScanQrCodeScreen.class);
                        startActivityForResult(intent, Constants.SCAN_QR_CODE_RESULT);
                    }
                    break;
                    case Screens.SPLASH_SCREEN: {
                        Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
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
