package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;

import im.adamant.android.ui.entities.messages.AbstractMessage;

import java.util.List;

public interface MessagesView extends MvpView {
    void showChatMessages(List<AbstractMessage> messages);
    void goToLastMessage();
    void changeTitle(String title);
    void messageWasSended(AbstractMessage message);
}
