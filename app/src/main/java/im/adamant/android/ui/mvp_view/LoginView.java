package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;

public interface LoginView extends MvpView {
    void setPassphrase(String passphrase);
    void invalidWords(CharSequence word, CharSequence suggestion1, CharSequence suggestion2);
    void invalidSymbol();
    void invalidCount(int currentCount, int necessaryCount);
    void invalidChecksum();
    void networkError(String errorString);
    void lockUI();
    void unlockUI();
}
