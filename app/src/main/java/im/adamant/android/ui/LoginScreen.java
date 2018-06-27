package im.adamant.android.ui;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.image.ImageType;

import im.adamant.android.AdamantApplication;
import im.adamant.android.Constants;
import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.helpers.QrCodeHelper;
import im.adamant.android.presenters.LoginPresenter;
import im.adamant.android.ui.mvp_view.LoginView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import butterknife.BindView;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.commands.Command;
import ru.terrakok.cicerone.commands.Forward;
import ru.terrakok.cicerone.commands.SystemMessage;


public class LoginScreen extends BaseActivity implements LoginView {

    @Inject
    @Named(value = Screens.LOGIN_SCREEN)
    QrCodeHelper qrCodeHelper;

    @Inject
    NavigatorHolder navigatorHolder;

    @Inject
    Provider<LoginPresenter> presenterProvider;

    //--Moxy
    @InjectPresenter
    LoginPresenter presenter;

    @ProvidePresenter
    public LoginPresenter getPresenter(){
        return presenterProvider.get();
    }

    //--ButterKnife
    @BindView(R.id.activity_login_et_pass_phrase) EditText passPhrase;
    @BindView(R.id.activity_login_et_new_passphrase) EditText newPassPhrase;
    @BindView(R.id.activity_login_cl_new_account_form) View newPassPhraseForm;
    @BindView(R.id.activity_login_btn_login) Button loginButton;

    //--Activity
    @Override
    public int getLayoutId() {
        return R.layout.activity_login_screen;
    }

    @Override
    public boolean withBackButton() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        //TODO: Необходим список ключевых слов единый для всех систем (Для генерации ключевых фраз)

        passPhrase.setOnFocusChangeListener( (view, isFocused) -> {
            if (!isFocused){
                AdamantApplication.hideKeyboard(this, passPhrase);
            }
        });
    }

    @OnClick(R.id.activity_login_btn_login)
    public void loginButtonClick() {
        presenter.onClickLoginButton(passPhrase.getText().toString());
        AdamantApplication.hideKeyboard(this, passPhrase);
    }

    @OnClick(R.id.activity_login_btn_generate_new_passphrase)
    public void generateNewPassphraseClick() {
        presenter.onClickGeneratePassphrase();
    }

    @OnClick(R.id.activity_login_btn_copy_new_passphrase)
    public void copyNewPassPhraseToClipboardClick() {
        ClipData clip = ClipData.newPlainText("passphrase", newPassPhrase.getText().toString());
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        if(clipboard != null){
            clipboard.setPrimaryClip(clip);
        }
    }

    @OnClick(R.id.activity_login_btn_scan_qrcode)
    public void scanQrCodeClick() {
        presenter.onClickScanQrCodeButton();
    }

    @OnClick(R.id.activity_login_btn_load_qrcode_from_gallery)
    public void loadQrCodeClick() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, Constants.IMAGE_FROM_GALLERY_SELECTED_RESULT);
    }

    @OnClick(R.id.activity_login_btn_create_qrcode)
    public void generateQrCodeClick(){
        TedPermission.with(this)
                .setRationaleMessage(R.string.rationale_qrcode_write_permission)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigatorHolder.setNavigator(navigator);
    }

    @Override
    protected void onPause() {
        super.onPause();
        navigatorHolder.removeNavigator();
    }

    @Override
    public void passPhraseWasGenerated(CharSequence passphrase) {
        newPassPhrase.setText(passphrase);
        newPassPhraseForm.setVisibility(View.VISIBLE);
    }

    @Override
    public void loginError(int resourceId) {
        Toast.makeText(getApplicationContext(), resourceId, Toast.LENGTH_LONG).show();
    }

    @Override
    public void lockAuthorization() {
        loginButton.setEnabled(false);
    }

    @Override
    public void unLockAuthorization() {
        loginButton.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null){return;}

        if (resultCode == RESULT_OK){
            switch (requestCode){
                case Constants.SCAN_QR_CODE_RESULT: {
                    if (data.getData() == null) {return;}

                    String qrCode = data.getData().toString();

                    if (qrCode != null){
                        passPhrase.setText(qrCode);
                        presenter.onClickLoginButton(qrCode);
                    }
                }
                break;

                case Constants.IMAGE_FROM_GALLERY_SELECTED_RESULT: {
                    if (data.getData() == null) {return;}

                    final Uri imageUri = data.getData();
                    try {
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        String qrCode = qrCodeHelper.parse(imageStream);

                        if (qrCode != null){
                            passPhrase.setText(qrCode);
                            presenter.onClickLoginButton(qrCode);
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
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
                switch (forward.getScreenKey()){
                    case Screens.SETTINGS_SCREEN:
                    case Screens.WALLET_SCREEN:
                    case Screens.CHATS_SCREEN: {
                        Bundle bundle = new Bundle();
                        bundle.putString(MainScreen.ARG_CURRENT_SCREEN, forward.getScreenKey());

                        Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                        intent.putExtras(bundle);

                        startActivity(intent);
                        finish();
                    }
                    break;
                    case Screens.SCAN_QRCODE_SCREEN: {
                        Intent intent = new Intent(getApplicationContext(), ScanQrCodeScreen.class);
                        startActivityForResult(intent, Constants.SCAN_QR_CODE_RESULT);
                    }
                    break;
                }
            } else if(command instanceof SystemMessage){
                SystemMessage message = (SystemMessage) command;
                Toast.makeText(getApplicationContext(), message.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    };

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            File qrCodeFile = qrCodeHelper.makeImageFile("pass_");
            try (OutputStream stream = new FileOutputStream(qrCodeFile)){
                QRCode.from(passPhrase.getText().toString()).to(ImageType.PNG).writeTo(stream);
                qrCodeHelper.registerImageInGallery(LoginScreen.this, qrCodeFile);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {

        }
    };
}
