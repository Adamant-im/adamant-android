package im.adamant.android.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.presenters.LoginPresenter;
import im.adamant.android.ui.mvp_view.LoginView;
import com.goterl.lazycode.lazysodium.LazySodium;


import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.commands.Command;
import ru.terrakok.cicerone.commands.Forward;
import ru.terrakok.cicerone.commands.SystemMessage;


public class LoginScreen extends BaseActivity implements LoginView {

    @Inject
    NavigatorHolder navigatorHolder;

    @Inject
    Provider<LoginPresenter> presenterProvider;

    //--Moxy
    @InjectPresenter
    LoginPresenter presenter;

    @ProvidePresenter
    public LoginPresenter getPresenter(){
        return presenterProvider.get();
    }

    //--ButterKnife
    @BindView(R.id.activity_login_et_pass_phrase) EditText passPhrase;
    @BindView(R.id.activity_login_et_new_passphrase) EditText newPassPhrase;
    @BindView(R.id.activity_login_cl_new_account_form) View newPassPhraseForm;
    @BindView(R.id.activity_login_btn_login) Button loginButton;

    @Inject
    LazySodium sodium;

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
        super.onCreate(savedInstanceState);

        //TODO: Необходим список ключевых слов единый для всех систем (Для генерации ключевых фраз)

        passPhrase.setOnFocusChangeListener( (view, isFocused) -> {
            if (!isFocused){
                hideKeyboard(passPhrase);
            }
        });
    }

    @OnClick(R.id.activity_login_btn_login)
    public void loginButtonClick() {
        presenter.onClickLoginButton(passPhrase.getText().toString());
        hideKeyboard(passPhrase);
    }

    @OnClick(R.id.activity_login_btn_generate_new_passphrase)
    public void generateNewPassphraseClick() {
        presenter.onClickGeneratePassphrase();
    }

    @OnClick(R.id.activity_login_btn_copy_new_passphrase)
    public void copyNewPassPhraseToClipboardClick() {
        ClipData clip = ClipData.newPlainText("passphrase", newPassPhrase.getText().toString());
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        if(clipboard != null){
            clipboard.setPrimaryClip(clip);
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
    public void passPhraseWasGenerated(CharSequence passphrase) {
        newPassPhrase.setText(passphrase);
        newPassPhraseForm.setVisibility(View.VISIBLE);
    }

    @Override
    public void loginError(int resourceId) {
        Toast.makeText(getApplicationContext(), resourceId, Toast.LENGTH_LONG).show();
    }

    @Override
    public void lockAuthorization() {
        loginButton.setEnabled(false);
    }

    @Override
    public void unLockAuthorization() {
        loginButton.setEnabled(true);
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
                        startActivity(new Intent(getApplicationContext(), MainScreen.class));
                        finish();
                    }
                }
            } else if(command instanceof SystemMessage){
                SystemMessage message = (SystemMessage) command;
                Toast.makeText(getApplicationContext(), message.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    };
}
