package im.adamant.android.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import butterknife.OnClick;
import im.adamant.android.Constants;
import im.adamant.android.R;
import im.adamant.android.helpers.AnimationUtils;
import im.adamant.android.helpers.QrCodeHelper;
import im.adamant.android.ui.ScanQrCodeScreen;
import im.adamant.android.ui.ShowQrCodeScreen;
import im.adamant.android.helpers.DrawableClickListener;
import im.adamant.android.ui.fragments.base.BaseBottomFragment;
import im.adamant.android.ui.fragments.base.BaseFragment;
import im.adamant.android.ui.mvp_view.CreateChatView;
import im.adamant.android.ui.presenters.CreateChatPresenter;

public class CreateChatFragment extends BaseBottomFragment implements CreateChatView {
    public static final String TAG = "CreateChatFragment";

    @Inject
    QrCodeHelper qrCodeHelper;

    @Inject
    Provider<CreateChatPresenter> presenterProvider;

    //--Moxy
    @InjectPresenter
    CreateChatPresenter createChatPresenter;

    @ProvidePresenter
    public CreateChatPresenter getPresenter(){
        return presenterProvider.get();
    }

    @BindView(R.id.fragment_create_chat_et_address) TextInputEditText addressView;
    @BindView(R.id.fragment_create_chat_il_layout) TextInputLayout addressLayoutView;
    @BindView(R.id.fragment_create_chat_btn_enter) MaterialButton startChatButtonView;
    @BindView(R.id.fragment_create_chat_btn_show_my_qr) MaterialButton showMyQrCOde;

    public static CreateChatFragment newInstance() {
        return new CreateChatFragment();
    }

    public CreateChatFragment() {
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_create_chat_screen;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        assert getArguments() != null;

        addressView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    createNewChatClick();
                    handled = true;
                }
                return handled;
            }
        });


        addressView.setOnTouchListener(new DrawableClickListener(TextInputEditText.class) {
            @Override
            protected void onClickStartDrawable(View v) {
                loadQrCodeClick();
            }

            @Override
            protected void onClickEndDrawable(View v) {
                scanQrCodeClick();
            }
        });

        showMyQrCOde.setPaintFlags(showMyQrCOde.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        return view;
    }

    @Override
    public void showError(int resourceId) {
        Toast.makeText(getContext(), getString(resourceId), Toast.LENGTH_LONG).show();
    }

    @Override
    public void lockUI() {
        startChatButtonView.setEnabled(false);
    }

    @Override
    public void unlockUI() {
        startChatButtonView.setEnabled(true);
    }

    @Override
    public void showQrCode(String content) {
        Activity activity = getActivity();
        if (activity != null){
            Bundle bundle = new Bundle();
            bundle.putString(ShowQrCodeScreen.ARG_DATA_FOR_QR_CODE, content);
            bundle.putString(Constants.KEY,Constants.PASSPHRASE);
            //bundle.putString("key","passphrase");
            Intent intent = new Intent(activity.getApplicationContext(), ShowQrCodeScreen.class);
            intent.putExtras(bundle);

            startActivity(intent);
        }
    }

    @Override
    public void close() {
        dismiss();
    }

    @OnClick(R.id.fragment_create_chat_btn_enter)
    public void createNewChatClick() {
        createChatPresenter.onClickCreateNewChat(
                addressView.getText().toString()
        );
    }

    public void scanQrCodeClick() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            Intent intent = new Intent(activity, ScanQrCodeScreen.class);
            startActivityForResult(intent, Constants.SCAN_QR_CODE_RESULT);
        }
    }

    public void loadQrCodeClick() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, Constants.IMAGE_FROM_GALLERY_SELECTED_RESULT);
    }

    @OnClick(R.id.fragment_create_chat_btn_show_my_qr)
    public void showMyQrCodeClick() {
        createChatPresenter.onClickShowMyQrCodeButton();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            String qrCode = qrCodeHelper.parseActivityResult(activity, requestCode, resultCode, data);

            if (!qrCode.isEmpty()){
                addressView.setText(qrCode);
                createChatPresenter.onClickCreateNewChat(qrCode);
                dismiss();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
