package im.adamant.android.ui;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.button.MaterialButton;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import im.adamant.android.Constants;
import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.interactors.AuthorizeInteractor;
import im.adamant.android.ui.adapters.WelcomeCardsAdapter;
import im.adamant.android.ui.custom_view.SimpleDotIndicatorDecoration;
import im.adamant.android.ui.fragments.BottomLoginFragment;
import im.adamant.android.ui.navigators.DefaultNavigator;
import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.commands.Back;
import ru.terrakok.cicerone.commands.BackTo;
import ru.terrakok.cicerone.commands.Command;
import ru.terrakok.cicerone.commands.Forward;
import ru.terrakok.cicerone.commands.Replace;
import ru.terrakok.cicerone.commands.SystemMessage;


public class LoginScreen extends BaseActivity implements HasSupportFragmentInjector {

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

    public static final String BOTTOM_LOGIN_TAG = "BottomLogin";

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

        loginFragment = (BottomLoginFragment) getSupportFragmentManager().findFragmentByTag(BOTTOM_LOGIN_TAG);

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
        Fragment fragment = supportFragmentManager.findFragmentByTag(BOTTOM_LOGIN_TAG);
        if (fragment == null) {
            loginFragment.show(supportFragmentManager, BOTTOM_LOGIN_TAG);
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

    private Navigator navigator = new DefaultNavigator(this) {
        @Override
        protected void forward(Forward forwardCommand) {
            switch (forwardCommand.getScreenKey()){
                case Screens.SETTINGS_SCREEN:
                case Screens.WALLET_SCREEN:
                case Screens.CHATS_SCREEN: {
                    Bundle bundle = new Bundle();
                    bundle.putString(MainScreen.ARG_CURRENT_SCREEN, forwardCommand.getScreenKey());

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
        }

        @Override
        protected void back(Back backCommand) {

        }

        @Override
        protected void backTo(BackTo backToCommand) {

        }

        @Override
        protected void message(SystemMessage systemMessageCommand) {
            Toast.makeText(getApplicationContext(), systemMessageCommand.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        protected void replace(Replace replaceCommand) {

        }
    };


}
