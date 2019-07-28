package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

import im.adamant.android.rx.AbstractObservableRxList;
import im.adamant.android.ui.messages_support.entities.AbstractMessage;
import im.adamant.android.ui.messages_support.entities.MessageListContent;

public interface MessagesView extends MvpView {

    @StateStrategyType(AddToEndSingleStrategy.class)
    void showChatMessages(AbstractObservableRxList<MessageListContent> messages);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void emptyView(boolean show);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void goToLastMessage();

    void dropMessageCost();
    void changeTitles(String title, String subTitle);
    void messageWasSended(AbstractMessage message);
    void showMessageCost(String cost);
    void showAvatarInTitle(String publicKey);

    @StateStrategyType(SkipStrategy.class)
    void showRenameDialog(String currentName);

    @StateStrategyType(SkipStrategy.class)
    void startSavingContacts();

    @StateStrategyType(SkipStrategy.class)
    void copyCompanionId(String companionId);

    @StateStrategyType(SkipStrategy.class)
    void showQrCodeCompanionId(String companionId);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void showLoading(boolean loading);
}
