package com.dremanovich.adamant_android.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.dremanovich.adamant_android.R;
import com.dremanovich.adamant_android.Screens;
import com.dremanovich.adamant_android.core.AdamantApi;
import com.dremanovich.adamant_android.presenters.LoginPresenter;
import com.dremanovich.adamant_android.ui.mvp_view.LoginView;
import com.goterl.lazycode.lazysodium.LazySodium;
import com.goterl.lazycode.lazysodium.exceptions.SodiumException;
import com.goterl.lazycode.lazysodium.interfaces.Sign;
import com.goterl.lazycode.lazysodium.utils.KeyPair;


import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
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

    @Inject
    LazySodium sodium;

    //--Activity
    @Override
    public int getLayoutId() {
        return R.layout.activity_login_screen;
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
    public void loginButtonClick(){
        presenter.onClickLoginButton(passPhrase.getText().toString());
        hideKeyboard(passPhrase);
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
                        startActivity(new Intent(getApplicationContext(), ChatsScreen.class));
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
