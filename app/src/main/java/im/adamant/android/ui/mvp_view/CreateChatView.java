package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;

public interface CreateChatView extends MvpView {
    void showError(int resourceId);
    void lockUI();
    void unlockUI();
}
