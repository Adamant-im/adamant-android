package im.adamant.android.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Pair;

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
import im.adamant.android.presenters.RegistrationPresenter;
import im.adamant.android.ui.adapters.PassphraseAdapter;
import im.adamant.android.ui.mvp_view.RegistrationView;
import im.adamant.android.ui.transformations.PassphraseAvatarTransformation;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class RegistrationScreen extends BaseActivity implements RegistrationView {

    @Inject
    Provider<RegistrationPresenter> presenterProvider;

    //--Moxy
    @InjectPresenter
    RegistrationPresenter presenter;

    @ProvidePresenter
    public RegistrationPresenter getPresenter(){
        return presenterProvider.get();
    }

    @Inject
    PassphraseAdapter passphraseAdapter;

    @Inject
    PassphraseAvatarTransformation avatarTransformation;

    @BindView(R.id.activity_registration_vp_carousel)
    DiscreteScrollView passphrasesListView;

    @BindView(R.id.activity_registration_et_pass_phrase)
    TextInputEditText inputPassphraseView;

    @BindView(R.id.activity_registration_il_layout)
    TextInputLayout inputLayoutView;

    @BindView(R.id.activity_registration_btn_create_address)
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
    }

    @OnClick(R.id.activity_registration_btn_refresh)
    public void onClickGenerateButton() {
        presenter.onClickGeneratePassphrases();
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
    protected void onResume() {
        super.onResume();
        Observable<String> obs = RxTextView
                .textChanges(inputPassphraseView)
                .filter(charSequence -> charSequence.length() > 0)
                .debounce(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
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
        compositeDisposable.dispose();
        compositeDisposable.clear();
    }

    private void showError(String error) {
        inputLayoutView.setError(error);
        inputPassphraseView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
        saveQrCodeButton.setEnabled(false);
        createAddressButton.setEnabled(false);
    }

    //    private void setColorToCompoundDrawables(TextView view, int colorResource) {
//        Drawable[] compoundDrawables = view.getCompoundDrawablesRelative();
//        for (Drawable drawable : compoundDrawables) {
//            if (drawable != null) {
//                DrawableCompat.setTint(drawable, ContextCompat.getColor(this, colorResource));
//            }
//        }
//    }
}
