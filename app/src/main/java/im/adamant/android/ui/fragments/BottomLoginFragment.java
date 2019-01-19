package im.adamant.android.ui.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputEditText;
import com.jakewharton.rxbinding3.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Provider;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import butterknife.BindView;
import butterknife.OnClick;
import im.adamant.android.AdamantApplication;
import im.adamant.android.Constants;
import im.adamant.android.R;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.helpers.QrCodeHelper;
import im.adamant.android.ui.presenters.LoginPresenter;
import im.adamant.android.ui.mvp_view.LoginView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BottomLoginFragment extends BaseBottomFragment implements LoginView {

    @Inject
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

    Disposable passphraseListener;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_bottom_login;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        passPhraseView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    onClickLoginButton();
                    handled = true;
                }
                return handled;
            }
        });


        return view;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                FrameLayout bottomSheet = (FrameLayout) dialog.findViewById(R.id.design_bottom_sheet);
                if (bottomSheet == null) { return; }

                BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
                if (behavior == null) { return; }

                behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View view, int newState) {
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                            handleDismissWindow();
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View view, float v) {

                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        passphraseListener = RxTextView
                .textChanges(passPhraseView)
                .filter(charSequence -> charSequence.length() > 0)
                .debounce(800, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .map(CharSequence::toString)
                .doOnNext(loginPresenter::onInputPassphrase)
                .doOnError(error -> LoggerHelper.e("ERR", error.getMessage(), error))
                .retry()
                .subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        passphraseListener.dispose();
        passphraseListener = null;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        handleDismissWindow();
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
       handleDismissWindow();
        super.onDismiss(dialog);
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
        FragmentActivity activity = getActivity();
        if (activity == null) {return;}
        AdamantApplication.hideKeyboard(activity, passPhraseView);

        String passphrase = (passPhraseView.getText() == null) ? "" : passPhraseView.getText().toString();
        loginPresenter.onClickLoginButton(passphrase);
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

    private void handleDismissWindow() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
            );
        }


    }
}
