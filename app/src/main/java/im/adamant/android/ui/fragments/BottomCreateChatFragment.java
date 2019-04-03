package im.adamant.android.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.rxbinding3.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Provider;

import androidx.fragment.app.FragmentActivity;
import butterknife.BindView;
import butterknife.OnClick;
import im.adamant.android.Constants;
import im.adamant.android.R;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.helpers.QrCodeHelper;
import im.adamant.android.ui.ScanQrCodeScreen;
import im.adamant.android.ui.ShowQrCodeScreen;
import im.adamant.android.ui.fragments.base.BaseBottomFragment;
import im.adamant.android.ui.mvp_view.CreateChatView;
import im.adamant.android.ui.presenters.CreateChatPresenter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class BottomCreateChatFragment extends BaseBottomFragment implements CreateChatView {

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

    private Disposable addressListener;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_create_chat_screen;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

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

        return view;
    }

    @Override
    public void showError(int resourceId) {
        addressLayoutView.setError(getString(resourceId));
    }

    @Override
    public void lockUI() {
        startChatButtonView.setEnabled(false);
    }

    @Override
    public void unlockUI() {
        startChatButtonView.setEnabled(true);
        addressLayoutView.setError("");
    }

    @Override
    public void showQrCode(String content) {
        Activity activity = getActivity();
        if (activity != null){
            Bundle bundle = new Bundle();
            bundle.putString(ShowQrCodeScreen.ARG_DATA_FOR_QR_CODE, content);

            Intent intent = new Intent(activity.getApplicationContext(), ShowQrCodeScreen.class);
            intent.putExtras(bundle);

            startActivity(intent);
        }
    }

    @OnClick(R.id.fragment_create_chat_btn_enter)
    public void createNewChatClick() {
        createChatPresenter.onClickCreateNewChat(
                addressView.getText().toString()
        );
        dismiss();
    }

    @OnClick(R.id.fragment_create_chat_btn_by_camera_qr)
    public void scanQrCodeClick() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            Intent intent = new Intent(activity, ScanQrCodeScreen.class);
            startActivityForResult(intent, Constants.SCAN_QR_CODE_RESULT);
        }
    }

    @OnClick(R.id.fragment_create_chat_btn_by_stored_qr)
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
    public void onResume() {
        super.onResume();
        addressListener = RxTextView
                .textChanges(addressView)
                .filter(charSequence -> charSequence.length() > 0)
                .debounce(800, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .map(CharSequence::toString)
                .doOnNext(createChatPresenter::onInputAddress)
                .doOnError(error -> LoggerHelper.e("ERR", error.getMessage(), error))
                .retry()
                .subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        addressListener.dispose();
        addressListener = null;
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
