package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;

public interface LoginView extends MvpView {
    void setPassphrase(String passphrase);
    void loginError(int resourceId);
    void lockUI();
    void unlockUI();
}
