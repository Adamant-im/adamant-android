package im.adamant.android.ui.mvp_view;

import android.util.Pair;

import com.arellomobile.mvp.MvpView;

import java.util.List;


public interface RegistrationView extends MvpView {
    void invalidWords(CharSequence word, CharSequence suggestion1, CharSequence suggestion2);
    void invalidSymbol();
    void invalidCount(int currentCount, int necessaryCount);
    void invalidChecksum();
    void onEnteredValidPassphrase();
    void updatePassphraseList(List<Pair<String, String>> passphrases);
    void showPassphrase(String passphrase);
}
