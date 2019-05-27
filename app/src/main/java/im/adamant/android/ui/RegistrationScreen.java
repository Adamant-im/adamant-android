package im.adamant.android.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.image.ImageType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import im.adamant.android.AdamantApplication;
import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.helpers.QrCodeHelper;
import im.adamant.android.ui.navigators.DefaultNavigator;
import im.adamant.android.ui.presenters.RegistrationPresenter;
import im.adamant.android.ui.adapters.PassphraseAdapter;
import im.adamant.android.ui.mvp_view.RegistrationView;
import im.adamant.android.ui.transformations.PassphraseAvatarTransformation;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.commands.Back;
import ru.terrakok.cicerone.commands.BackTo;
import ru.terrakok.cicerone.commands.Command;
import ru.terrakok.cicerone.commands.Forward;
import ru.terrakok.cicerone.commands.Replace;
import ru.terrakok.cicerone.commands.SystemMessage;


//TODO: Optimize UI
public class RegistrationScreen extends BaseActivity implements RegistrationView {

    @Inject
    QrCodeHelper qrCodeHelper;

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

    @BindView(R.id.activity_registration_et_passphrase)
    TextInputEditText inputPassphraseView;

    @BindView(R.id.activity_registration_il_layout)
    TextInputLayout inputLayoutView;

//    @BindView(R.id.activity_registration_btn_register)
//    MaterialButton createAddressButton;

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

        inputPassphraseView.setKeyListener(null);

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

                    if(clipboard != null) {
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(this, R.string.address_was_copied, Toast.LENGTH_LONG).show();
                    }

                    return true;
                }
            }
            return false;
        });

        inputPassphraseView.setOnFocusChangeListener((view, isFocused) -> {
            if (!isFocused) {
                AdamantApplication.hideKeyboard(this, inputPassphraseView);
            }
        });

        saveQrCodeButton.setPaintFlags(saveQrCodeButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

//    @OnClick(R.id.activity_registration_btn_refresh)
//    public void onClickGenerateButton() {
//        presenter.onClickGeneratePassphrases();
//    }

//    @OnClick(R.id.activity_registration_btn_register)
//    public void onClickRegisterButton() {
//        presenter.onClickRegisterAccount();
//    }

    @OnClick(R.id.activity_registration_btn_save_qr)
    public void onClickSaveQrCode() {
        Bundle bundle = new Bundle();
        bundle.putString(ShowQrCodeScreen.ARG_DATA_FOR_QR_CODE, inputPassphraseView.getText().toString());

        Intent intent = new Intent(getApplicationContext(), ShowQrCodeScreen.class);
        intent.putExtras(bundle);

        startActivity(intent);
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
        Drawable copyButton = ContextCompat.getDrawable(this, R.drawable.ic_copy);

        inputLayoutView.setError("");
        inputPassphraseView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, copyButton, null);
        saveQrCodeButton.setEnabled(true);
//        createAddressButton.setEnabled(true);
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
//        createAddressButton.setEnabled(false);
        passphrasesListView.setEnabled(false);
        saveQrCodeButton.setEnabled(false);
    }

    @Override
    public void unlockUI() {
        inputPassphraseView.setEnabled(true);
//        createAddressButton.setEnabled(true);
        passphrasesListView.setEnabled(true);
        saveQrCodeButton.setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        navigatorHolder.setNavigator(navigator);

        //TODO: Refactor. The user can no longer enter the text by himself, this code is not necessary.
        Observable<String> obs = RxTextView
                .textChanges(inputPassphraseView)
                .filter(charSequence -> charSequence.length() > 0)
                .debounce(800, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .map(CharSequence::toString)
                .doOnNext(presenter::onInputPassphrase)
                .doOnError(error -> LoggerHelper.e("ERR", error.getMessage(), error))
                .retry();

        Disposable subscribe = obs.subscribe();

        compositeDisposable.add(subscribe);

        Disposable clickSubscription = passphraseAdapter
                .getObservable()
                .doOnNext(index -> passphrasesListView.smoothScrollToPosition(index))
                .doOnError(error -> LoggerHelper.e("ERR", error.getMessage(), error))
                .retry()
                .subscribe();

        compositeDisposable.add(clickSubscription);    }

    @Override
    protected void onPause() {
        super.onPause();

        navigatorHolder.removeNavigator();

        compositeDisposable.dispose();
        compositeDisposable.clear();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AdamantApplication.hideKeyboard(this, inputPassphraseView);
    }

    private void showError(String error) {
        inputLayoutView.setError(error);
        inputPassphraseView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
        saveQrCodeButton.setEnabled(false);
//        createAddressButton.setEnabled(false);
    }


    private Navigator navigator = new DefaultNavigator(this) {
        @Override
        protected void forward(Forward forwardCommand) {
            switch (forwardCommand.getScreenKey()) {
                case Screens.WALLET_SCREEN:
                case Screens.SETTINGS_SCREEN:
                case Screens.CHATS_SCREEN: {
                    Bundle bundle = new Bundle();
                    bundle.putString(MainScreen.ARG_CURRENT_SCREEN, forwardCommand.getScreenKey());

                    Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                    intent.putExtras(bundle);

                    startActivity(intent);
                    finish();
                }
                break;
            }
        }

        @Override
        protected void back(Back backCommand) {

        }

        @Override
        protected void backTo(BackTo backToCommand) {

        }

        @Override
        protected void message(SystemMessage systemMessageCommand) {
            Toast.makeText(getApplicationContext(), systemMessageCommand.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        protected void replace(Replace replaceCommand) {

        }

    };

}
