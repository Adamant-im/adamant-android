package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import im.adamant.android.ui.entities.Chat;

import java.util.List;

public interface ChatsView extends MvpView {
//    @StateStrategyType(SkipStrategy.class)
    void showChats(List<Chat> chats);
    void progress(boolean value);
}
