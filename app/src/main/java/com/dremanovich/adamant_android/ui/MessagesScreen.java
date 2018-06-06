package com.dremanovich.adamant_android.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.dremanovich.adamant_android.R;
import com.dremanovich.adamant_android.presenters.MessagesPresenter;
import com.dremanovich.adamant_android.ui.adapters.MessagesAdapter;
import com.dremanovich.adamant_android.ui.entities.Chat;
import com.dremanovich.adamant_android.ui.entities.Message;
import com.dremanovich.adamant_android.ui.mvp_view.MessagesView;

import java.util.List;
import java.util.SortedSet;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import ru.terrakok.cicerone.NavigatorHolder;

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
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        messagesList.setLayoutManager(layoutManager);
        messagesList.setAdapter(adapter);

        if (getIntent() != null && getIntent().hasExtra(ARG_CHAT)){
            Chat currentChat = (Chat) getIntent().getSerializableExtra(ARG_CHAT);
            presenter.onShowChat(currentChat);
        }

        //TODO: fix bug: when user scrolled recyclerview to top, adapter will scroll recyclerview to down
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                goToLastMessage();
                super.onChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void showChatMessages(List<Message> messages) {
        if (messages != null){
            adapter.updateDataset(
                    messages
            );
        }
    }

    @Override
    public void goToLastMessage() {
        messagesList.scrollToPosition(
                adapter.getItemCount() - 1
        );
    }

    @OnClick(R.id.activity_messages_btn_send)
    protected void onClickSendButton() {
        presenter.onClickSendMessage(
            newMessageText.getText().toString()
        );

        newMessageText.setText("");
    }
}
