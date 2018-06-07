package com.dremanovich.adamant_android.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.dremanovich.adamant_android.R;
import com.dremanovich.adamant_android.Screens;
import com.dremanovich.adamant_android.presenters.CreateChatPresenter;
import com.dremanovich.adamant_android.ui.mvp_view.CreateChatView;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.commands.Command;
import ru.terrakok.cicerone.commands.Forward;

public class CreateChatScreen extends BaseActivity implements CreateChatView {

    @Inject
    NavigatorHolder navigatorHolder;

    @Inject
    Provider<CreateChatPresenter> presenterProvider;

    //--Moxy
    @InjectPresenter
    CreateChatPresenter presenter;

    @ProvidePresenter
    public CreateChatPresenter getPresenter(){
        return presenterProvider.get();
    }

    //--ButterKnife
    @BindView(R.id.activity_create_chat_et_address) EditText addresView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_chat_screen;
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

    @OnClick(R.id.activity_create_chat_btn_create)
    public void createNewChatClick() {
        presenter.onClickCreateNewChat(
                addresView.getText().toString()
        );
    }

    @Override
    public void showError(int resourceId) {
        Toast.makeText(this, resourceId, Toast.LENGTH_LONG).show();
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
                    case Screens.MESSAGES_SCREEN: {
                        Intent intent = new Intent(getApplicationContext(), MessagesScreen.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(MessagesScreen.ARG_CHAT, (Serializable) forward.getTransitionData());
                        intent.putExtras(bundle);

                        startActivity(intent);
                    }
                }
            }
        }
    };
}
