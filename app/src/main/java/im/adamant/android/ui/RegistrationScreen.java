package im.adamant.android.ui;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.MotionEvent;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.presenters.RegistrationPresenter;
import im.adamant.android.ui.adapters.PassphraseAdapter;
import im.adamant.android.ui.mvp_view.RegistrationView;
import im.adamant.android.ui.transformations.PassphraseAvatarTransformation;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.commands.Command;
import ru.terrakok.cicerone.commands.Forward;
import ru.terrakok.cicerone.commands.SystemMessage;


//TODO: Optimize UI
public class RegistrationScreen extends BaseActivity implements RegistrationView {

    @Inject
    NavigatorHolder navigatorHolder;

    @Inject
    Provider<RegistrationPresenter> presenterProvider;

    @Inject
    PassphraseAdapter passphraseAdapter;

    @Inject
    PassphraseAvatarTransformation avatarTransformation;

    //--Moxy
    @InjectPresenter
    RegistrationPresenter presenter;

    @ProvidePresenter
    public RegistrationPresenter getPresenter(){
        return presenterProvider.get();
    }


    @BindView(R.id.activity_registration_vp_carousel)
    DiscreteScrollView passphrasesListView;

    @BindView(R.id.fragment_login_et_passphrase)
    TextInputEditText inputPassphraseView;

    @BindView(R.id.activity_registration_il_layout)
    TextInputLayout inputLayoutView;

    @BindView(R.id.activity_registration_btn_register)
    MaterialButton createAddressButton;

    @BindView(R.id.activity_registration_btn_save_qr)
    MaterialButton saveQrCodeButton;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public int getLayoutId() {
        return R.layout.activity_registration_screen;
    }

    @Override
    public boolean withBackButton() {
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.activity_registration_tv_title));

        passphrasesListView.setAdapter(passphraseAdapter);
        passphrasesListView.setOffscreenItems(3);
        passphrasesListView.setOverScrollEnabled(false);

        passphrasesListView.setItemTransformer(avatarTransformation);

        passphrasesListView.addOnItemChangedListener(((viewHolder, i) -> {
            presenter.onSelectedPassphrase(i);
        }));

        inputPassphraseView.setOnTouchListener((v, event) -> {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;

            if(event.getAction() == MotionEvent.ACTION_UP) {
                Drawable drawable = inputPassphraseView.getCompoundDrawablesRelative()[DRAWABLE_RIGHT];

                if (drawable == null) {
                    return false;
                }

                if(event.getRawX() >= (inputPassphraseView.getRight() - drawable.getBounds().width())) {
                    ClipData clip = ClipData.newPlainText("passphrase", inputPassphraseView.getText().toString());
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

                    if(clipboard != null){
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(this, R.string.address_was_copied, Toast.LENGTH_LONG).show();
                    }

                    return true;
                }
            }
            return false;
        });
    }

    @OnClick(R.id.activity_registration_btn_refresh)
    public void onClickGenerateButton() {
        presenter.onClickGeneratePassphrases();
    }

    @OnClick(R.id.activity_registration_btn_register)
    public void onClickRegisterButton() {
        presenter.onClickRegisterAccount();
    }

    @Override
    public void invalidWords(CharSequence word, CharSequence suggestion1, CharSequence suggestion2) {
        String patternString = getString(R.string.invalid_word_in_passphrase);
        patternString = String.format(Locale.ENGLISH, patternString, word, suggestion1, suggestion2);
        showError(patternString);
    }

    @Override
    public void invalidSymbol() {
        showError(getString(R.string.invalid_symbol_in_passphrase));
    }

    @Override
    public void invalidCount(int currentCount, int necessaryCount) {
        String patternString = getString(R.string.invalid_count_words_in_passphrase);
        patternString = String.format(Locale.ENGLISH, patternString, currentCount, necessaryCount);
        showError(patternString);
    }

    @Override
    public void invalidChecksum() {
        showError(getString(R.string.invalid_checksum_in_passphrase));
    }

    @Override
    public void onEnteredValidPassphrase() {
        Drawable copyButton = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            copyButton = getResources().getDrawable(R.drawable.ic_copy, getTheme());
        } else {
            copyButton = getResources().getDrawable(R.drawable.ic_copy);
        }
        inputLayoutView.setError("");
        inputPassphraseView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, copyButton, null);
        saveQrCodeButton.setEnabled(true);
        createAddressButton.setEnabled(true);
    }

    @Override
    public void updatePassphraseList(List<Pair<String, String>> passphrases) {
        passphraseAdapter.setPassphrases(passphrases);
    }

    @Override
    public void showPassphrase(String passphrase) {
        inputPassphraseView.setText(passphrase);
        inputPassphraseView.requestFocus();
    }

    @Override
    public void lockUI() {
        inputPassphraseView.setEnabled(false);
        createAddressButton.setEnabled(false);
        passphrasesListView.setEnabled(false);
        saveQrCodeButton.setEnabled(false);
    }

    @Override
    public void unlockUI() {
        inputPassphraseView.setEnabled(true);
        createAddressButton.setEnabled(true);
        passphrasesListView.setEnabled(true);
        saveQrCodeButton.setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        navigatorHolder.setNavigator(navigator);

        Observable<String> obs = RxTextView
                .textChanges(inputPassphraseView)
                .filter(charSequence -> charSequence.length() > 0)
                .debounce(800, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .map(CharSequence::toString)
                .doOnNext(presenter::onInputPassphrase)
                .retry();

        Disposable subscribe = obs.subscribe();

        compositeDisposable.add(subscribe);

        Disposable clickSubscription = passphraseAdapter
                .getObservable()
                .doOnNext(index -> passphrasesListView.smoothScrollToPosition(index))
                .retry()
                .subscribe();

        compositeDisposable.add(clickSubscription);
    }

    @Override
    protected void onPause() {
        super.onPause();

        navigatorHolder.removeNavigator();

        compositeDisposable.dispose();
        compositeDisposable.clear();
    }

    private void showError(String error) {
        inputLayoutView.setError(error);
        inputPassphraseView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
        saveQrCodeButton.setEnabled(false);
        createAddressButton.setEnabled(false);
    }


    private Navigator navigator = new Navigator() {
        @Override
        public void applyCommands(Command[] commands) {
            for (Command command : commands){
                apply(command);
            }
        }

        private void apply(Command command){
            if (command instanceof Forward) {
                Forward forward = (Forward)command;
                switch (forward.getScreenKey()) {
                    case Screens.WALLET_SCREEN:
                    case Screens.SETTINGS_SCREEN:
                    case Screens.CHATS_SCREEN: {
                        Bundle bundle = new Bundle();
                        bundle.putString(MainScreen.ARG_CURRENT_SCREEN, forward.getScreenKey());

                        Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                        intent.putExtras(bundle);

                        startActivity(intent);
                        finish();
                    }
                    break;
                }
            } else if(command instanceof SystemMessage) {
                SystemMessage message = (SystemMessage) command;
                Toast.makeText(getApplicationContext(), message.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    };
}
