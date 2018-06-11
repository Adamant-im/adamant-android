package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;

import im.adamant.android.ui.entities.Message;

import java.util.List;

public interface MessagesView extends MvpView {
    void showChatMessages(List<Message> messages);
    void goToLastMessage();
    void changeTitle(String title);
}
