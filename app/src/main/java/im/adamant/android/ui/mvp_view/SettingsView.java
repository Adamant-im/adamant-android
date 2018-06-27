package im.adamant.android.ui.mvp_view;

import com.arellomobile.mvp.MvpView;

import java.util.List;

import im.adamant.android.core.entities.ServerNode;

public interface SettingsView extends MvpView {
    void clearNodeTextField();
    void hideKeyboard();
}
