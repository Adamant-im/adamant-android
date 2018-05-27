package com.dremanovich.adamant_android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;
import com.dremanovich.adamant_android.ui.entities.Chat;

import java.util.List;

public interface ChatsView extends MvpView {
    void showChats(List<Chat> chats);
}
