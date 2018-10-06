package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;

import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import im.adamant.android.ui.messages_support.entities.MessageListContent;

import java.util.List;

public interface MessagesView extends MvpView {
    void showChatMessages(List<MessageListContent> messages);
    void goToLastMessage();
    void changeTitles(String title, String subTitle);
    void messageWasSended(AbstractMessage message);
    void showMessageCost(String cost);
}
