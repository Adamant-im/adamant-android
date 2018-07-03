package im.adamant.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import im.adamant.android.Constants;
import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.helpers.QrCodeHelper;
import im.adamant.android.presenters.CreateChatPresenter;
import im.adamant.android.ui.mvp_view.CreateChatView;

import java.io.Serializable;

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

public class CreateChatScreen extends BaseActivity implements CreateChatView {

    @Inject
    NavigatorHolder navigatorHolder;

    @Named(Screens.CREATE_CHAT_SCREEN)
    @Inject
    QrCodeHelper qrCodeHelper;

    @Inject
    Provider<CreateChatPresenter> presenterProvider;

    //--Moxy
    @InjectPresenter
    CreateChatPresenter presenter;

    @ProvidePresenter
    public CreateChatPresenter getPresenter(){
        return presenterProvider.get();
    }

    //--ButterKnife
    @BindView(R.id.activity_create_chat_et_address) EditText addressView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_chat_screen;
    }

    @Override
    public boolean withBackButton() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
    }

    @OnClick(R.id.activity_create_chat_btn_create)
    public void createNewChatClick() {
        presenter.onClickCreateNewChat(
                addressView.getText().toString()
        );
    }

    @OnClick(R.id.activity_create_chat_ibtn_scan_qrcode)
    public void scanQrCodeClick() {
        presenter.onClickScanQrCodeButton();
    }

    @OnClick(R.id.activity_create_chat_ibtn_qrcode_from_gallery)
    public void loadQrCodeClick() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, Constants.IMAGE_FROM_GALLERY_SELECTED_RESULT);
    }

    @Override
    public void showError(int resourceId) {
        Toast.makeText(this, resourceId, Toast.LENGTH_LONG).show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String qrCode = qrCodeHelper.parseActivityResult(this, requestCode, resultCode, data);

        if (!qrCode.isEmpty()){
            addressView.setText(qrCode);
            presenter.onClickCreateNewChat(qrCode);
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
                    case Screens.MESSAGES_SCREEN: {
                        Intent intent = new Intent(getApplicationContext(), MessagesScreen.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(MessagesScreen.ARG_CHAT, (Serializable) forward.getTransitionData());
                        intent.putExtras(bundle);

                        startActivity(intent);
                    }
                    break;

                    case Screens.SCAN_QRCODE_SCREEN: {
                        Intent intent = new Intent(getApplicationContext(), ScanQrCodeScreen.class);
                        startActivityForResult(intent, Constants.SCAN_QR_CODE_RESULT);
                    }
                    break;
                }
            }
        }
    };
}
