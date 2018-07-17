package im.adamant.android.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import im.adamant.android.AdamantApplication;
import im.adamant.android.R;
import im.adamant.android.presenters.MessagesPresenter;
import im.adamant.android.ui.adapters.MessagesAdapter;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.entities.messages.AbstractMessage;
import im.adamant.android.ui.mvp_view.MessagesView;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.commands.Command;
import ru.terrakok.cicerone.commands.SystemMessage;

public class MessagesScreen extends BaseActivity implements MessagesView {
    public static final String ARG_CHAT = "chat";

    @Inject
    NavigatorHolder navigatorHolder;

    @Inject
    Provider<MessagesPresenter> presenterProvider;

    @Inject
    MessagesAdapter adapter;

    //--Moxy
    @InjectPresenter
    MessagesPresenter presenter;

    @ProvidePresenter
    public MessagesPresenter getPresenter(){
        return presenterProvider.get();
    }

    //--ButterKnife
    @BindView(R.id.activity_messages_rv_messages) RecyclerView messagesList;
    @BindView(R.id.activity_messages_et_new_msg_text) EditText newMessageText;
    @BindView(R.id.activity_messages_btn_send) Button buttonSend;


    //--Activity
    @Override
    public int getLayoutId() {
        return R.layout.activity_messages_screen;
    }

    @Override
    public boolean withBackButton() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        messagesList.setLayoutManager(layoutManager);
        messagesList.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent != null){
            if (intent.hasExtra(ARG_CHAT)){
                Chat currentChat = (Chat) getIntent().getSerializableExtra(ARG_CHAT);
                presenter.onShowChat(currentChat);
            }

            if (Intent.ACTION_VIEW.equals(intent.getAction())){
                Uri uri = intent.getData();
                if (uri != null){
                    String address = uri.getQueryParameter("address");
                    String label = uri.getQueryParameter("label");

                    presenter.onShowChatByAddress(address, label);
                }
            }
        }

        newMessageText.setOnFocusChangeListener( (view, isFocused) -> {
            if (!isFocused){
                AdamantApplication.hideKeyboard(this, newMessageText);
            }
        });
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
    public void showChatMessages(List<AbstractMessage> messages) {
        if (messages != null){
            adapter.updateDataset(
                    messages
            );

            goToLastMessage();
        }
    }

    @Override
    public void goToLastMessage() {
        messagesList.scrollToPosition(
                adapter.getItemCount() - 1
        );
    }

    @Override
    public void changeTitle(String title) {
        setTitle(title);
    }

    @Override
    public void messageWasSended(AbstractMessage message) {
        if (balanceUpdateService != null){
            balanceUpdateService.updateBalanceImmediately();
        }
    }

    @OnClick(R.id.activity_messages_btn_send)
    protected void onClickSendButton() {
        presenter.onClickSendMessage(
            newMessageText.getText().toString()
        );

        newMessageText.setText("");
    }

    private Navigator navigator = new Navigator() {
        @Override
        public void applyCommands(Command[] commands) {
            for (Command command : commands){
                apply(command);
            }
        }

        private void apply(Command command){
            if(command instanceof SystemMessage){
                SystemMessage message = (SystemMessage) command;
                Toast.makeText(getApplicationContext(), message.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    };
}
