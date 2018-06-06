package com.dremanovich.adamant_android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;

public interface LoginView extends MvpView {
    void passPhraseWasGenerated(CharSequence passphrase);
    void loginError(int resourceId);
    void lockAuthorization();
    void unLockAuthorization();
}
