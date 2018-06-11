package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;
import im.adamant.android.ui.entities.Chat;

import java.util.List;

public interface ChatsView extends MvpView {
    void showChats(List<Chat> chats);
}
