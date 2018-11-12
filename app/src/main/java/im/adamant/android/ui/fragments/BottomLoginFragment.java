package im.adamant.android.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.AndroidSupportInjection;
import im.adamant.android.Constants;
import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.helpers.QrCodeHelper;
import im.adamant.android.presenters.LoginPresenter;
import im.adamant.android.presenters.MainPresenter;
import im.adamant.android.ui.mvp_view.LoginView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BottomLoginFragment extends BaseBottomFragment implements LoginView {

    @Inject
    @Named(value = Screens.LOGIN_SCREEN)
    QrCodeHelper qrCodeHelper;

    @Inject
    Provider<LoginPresenter> presenterProvider;

    //--Moxy
    @InjectPresenter
    LoginPresenter loginPresenter;

    @ProvidePresenter
    public LoginPresenter getPresenter(){
        return presenterProvider.get();
    }

    @BindView(R.id.fragment_login_et_passphrase) TextInputEditText passPhraseView;
    @BindView(R.id.fragment_login_btn_enter) Button loginButtonView;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public int getLayoutId() {
        return R.layout.fragment_bottom_login;
    }

    @Override
    public void onResume() {
        super.onResume();

        Observable<String> obs = RxTextView
                .textChanges(passPhraseView)
                .debounce(800, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .map(CharSequence::toString)
                .doOnNext(loginPresenter::onInputPassphrase)
                .retry();

        compositeDisposable.add(obs.subscribe());

    }

    @Override
    public void onPause() {
        super.onPause();

        compositeDisposable.dispose();
        compositeDisposable.clear();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentActivity activity = getActivity();
        if (activity == null) {return;}

        String qrCode = qrCodeHelper.parseActivityResult(activity, requestCode, resultCode, data);

        if (!qrCode.isEmpty()){
            passPhraseView.setText(qrCode);
            loginPresenter.onClickLoginButton(qrCode);
        }
    }

    @OnClick(R.id.fragment_login_btn_enter)
    public void onClickLoginButton() {
        loginPresenter.onClickLoginButton(passPhraseView.getText().toString());
    }

    @OnClick(R.id.fragment_login_btn_by_camera_qr)
    public void onClickLoginByCameraQRCode() {
        loginPresenter.onClickScanQrCodeButton();
    }

    @OnClick(R.id.fragment_login_btn_by_stored_qr)
    public void onClickLoginByFileQRCode() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, Constants.IMAGE_FROM_GALLERY_SELECTED_RESULT);
    }

    @Override
    public void setPassphrase(String passphrase) {
        passPhraseView.setText(passphrase);
    }

    @Override
    public void loginError(int resourceId) {
        FragmentActivity activity = getActivity();
        if (activity != null){
            Toast.makeText(activity.getApplicationContext(), resourceId, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void lockUI() {
        loginButtonView.setEnabled(false);
    }

    @Override
    public void unlockUI() {
        loginButtonView.setEnabled(true);
    }
}
