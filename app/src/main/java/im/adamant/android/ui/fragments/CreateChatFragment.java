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
import im.adamant.android.ui.fragments.base.BaseFragment;
import im.adamant.android.ui.mvp_view.CreateChatView;
import im.adamant.android.ui.presenters.CreateChatPresenter;

public class CreateChatFragment extends BaseFragment implements CreateChatView, AnimationUtils.Dismissible {
    public static final String ARG_REVEAL_SETTINGS = "ARG_REVEAL_SETTINGS";
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

//    private Disposable addressListener;
    private AnimationUtils.AnimationFinishedListener animationFinishedListener;

    public static CreateChatFragment newInstance(
            AnimationUtils.RevealAnimationSetting animationSetting,
            AnimationUtils.AnimationFinishedListener listener
    ) {
        CreateChatFragment createChatFragment = new CreateChatFragment(listener);
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_REVEAL_SETTINGS, animationSetting);
        createChatFragment.setArguments(bundle);

        return createChatFragment;
    }

    public CreateChatFragment(AnimationUtils.AnimationFinishedListener animationFinishedListener) {
        this.animationFinishedListener = animationFinishedListener;
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
        AnimationUtils.startCreateChatRevealShowAnimation(
                getContext(),
                view,
                getArguments().getParcelable(ARG_REVEAL_SETTINGS),
                () -> {});

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

    @OnClick(R.id.fragment_create_chat_cl_faded_layout)
    public void clickOnFadedLayout() {
        dismiss();
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

//    @Override
//    public void onResume() {
//        super.onResume();
//        addressListener = RxTextView
//                .textChanges(addressView)
//                .filter(charSequence -> charSequence.length() > 0)
//                .debounce(800, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
//                .map(CharSequence::toString)
//                .doOnNext(createChatPresenter::onInputAddress)
//                .doOnError(error -> LoggerHelper.e("ERR", error.getMessage(), error))
//                .retry()
//                .subscribe();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        addressListener.dispose();
//        addressListener = null;
//    }

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

    @Override
    public void dismiss() {
        assert getArguments() != null;
        AnimationUtils.startCreateChatRevealExitAnimation(
                getContext(),
                getView(),
                getArguments().getParcelable(ARG_REVEAL_SETTINGS),
                animationFinishedListener
        );
    }
}
