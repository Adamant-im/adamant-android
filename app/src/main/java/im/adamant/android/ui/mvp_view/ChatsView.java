package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

import im.adamant.android.ui.entities.Chat;

@StateStrategyType(AddToEndSingleStrategy.class)
public interface ChatsView extends MvpView {
//    @StateStrategyType(SkipStrategy.class)
    void showChats(List<Chat> chats);
    void progress(boolean value);
}
