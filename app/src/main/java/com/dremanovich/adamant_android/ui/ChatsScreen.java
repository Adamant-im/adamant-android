package com.dremanovich.adamant_android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.dremanovich.adamant_android.R;
import com.dremanovich.adamant_android.Screens;
import com.dremanovich.adamant_android.presenters.ChatsPresenter;
import com.dremanovich.adamant_android.ui.adapters.ChatsAdapter;
import com.dremanovich.adamant_android.ui.entities.Chat;
import com.dremanovich.adamant_android.ui.mvp_view.ChatsView;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import dagger.android.AndroidInjection;
import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.commands.Command;
import ru.terrakok.cicerone.commands.Forward;
import ru.terrakok.cicerone.commands.SystemMessage;

public class ChatsScreen extends BaseActivity implements ChatsView, ChatsAdapter.SelectItemListener {

    @Inject
    NavigatorHolder navigatorHolder;

    @Inject
    Provider<ChatsPresenter> presenterProvider;

    @Inject
    ChatsAdapter adapter;

    //--Moxy
    @InjectPresenter
    ChatsPresenter presenter;

    @ProvidePresenter
    public ChatsPresenter getPresenter(){
        return presenterProvider.get();
    }

    //--ButterKnife
    @BindView(R.id.activity_chats_rv_chats) RecyclerView chatList;


    //--Activity
    @Override
    public int getLayoutId() {
        return R.layout.activity_chats_screen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        chatList.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                chatList.getContext(),
                DividerItemDecoration.VERTICAL
        );
        chatList.addItemDecoration(dividerItemDecoration);
        chatList.setAdapter(adapter);
    }

    @Override
    public void showChats(List<Chat> chats) {
        adapter.updateDataset(chats);
    }

    @Override
    public void itemWasSelected(Chat chat) {
        presenter.onChatWasSelected(chat);
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
            } else if(command instanceof SystemMessage){
                SystemMessage message = (SystemMessage) command;
                Toast.makeText(getApplicationContext(), message.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    };
}
