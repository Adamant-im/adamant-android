package im.adamant.android.ui.fragments;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Provider;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import butterknife.BindView;
import butterknife.OnClick;
import im.adamant.android.R;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.helpers.DrawableColorHelper;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
import im.adamant.android.ui.BaseActivity;
import im.adamant.android.ui.mvp_view.SendCurrencyTransferView;
import im.adamant.android.ui.presenters.SendCurrencyPresenter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class SendCurrencyFragment extends BaseFragment implements SendCurrencyTransferView {
    public static final String ARG_WALLET_FACADE_TYPE = "wallet_facade_type";
    public static final String ARG_COMPANION_ID = "companion_id";

    @Inject
    Provider<SendCurrencyPresenter> presenterProvider;

    //--Moxy
    @InjectPresenter
    SendCurrencyPresenter presenter;

    @ProvidePresenter
    public SendCurrencyPresenter getPresenter(){
        return presenterProvider.get();
    }

    @BindView(R.id.list_item_currency_send_address) TextInputEditText recipientAddressView;
    @BindView(R.id.list_item_currency_send_il_address_layout) TextInputLayout recipientAddressLayoutView;
    @BindView(R.id.list_item_currency_amount) TextInputEditText amountView;
    @BindView(R.id.list_item_currency_send_il_amount_layout) TextInputLayout amountLayoutView;
    @BindView(R.id.list_item_currency_send_fee) TextInputEditText feeView;
    @BindView(R.id.list_item_currency_send_il_fee_layout) TextInputLayout feeLayoutView;
    @BindView(R.id.list_item_currency_send_total_amount) TextInputEditText totalAmountView;
    @BindView(R.id.list_item_currency_send_il_total_amount_layout) TextInputLayout totalAmountLayoutView;
    @BindView(R.id.list_item_currency_send_ll_not_supported) LinearLayout notSupportedView;
    @BindView(R.id.list_item_currency_send_cl_supported) ConstraintLayout supportedView;
    @BindView(R.id.list_item_currency_send_ll_content_container) LinearLayout contentContainerView;
    @BindView(R.id.list_item_currency_send_remainder) TextView reminderView;
    @BindView(R.id.list_item_currency_send_comment) TextInputEditText commentView;
    @BindView(R.id.list_item_currency_send_il_comment_layout) TextInputLayout commentLayoutView;
    @BindView(R.id.list_item_currency_send_btn_send) MaterialButton sendButtonView;

    private CompositeDisposable subscriptions = new CompositeDisposable();


    public static SendCurrencyFragment newInstance(
            SupportedWalletFacadeType facadeType,
            String companionId
    ) {
        SendCurrencyFragment pageFragment = new SendCurrencyFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(ARG_WALLET_FACADE_TYPE, facadeType);
        arguments.putString(ARG_COMPANION_ID, companionId);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_currency_send_view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            SupportedWalletFacadeType type = (SupportedWalletFacadeType) arguments.getSerializable(ARG_WALLET_FACADE_TYPE);
            String companionId = arguments.getString(ARG_COMPANION_ID);

            if (type != null && companionId != null) {
                presenter.setCompanionIdAndFacadeType(companionId, type);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        FragmentActivity activity = getActivity();
        if (activity != null){
            DrawableColorHelper.changeColorForDrawable(activity, recipientAddressView, R.color.inactiveInputOutline, PorterDuff.Mode.SRC_IN);

            RxTextView
                    .textChanges(amountView)
                    .filter(charSequence -> charSequence.length() > 0)
                    .debounce(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                    .map(charSequence -> new BigDecimal(charSequence.toString()))
                    .doOnNext(presenter::onEnterAmount)
                    .doOnError(error -> {
                        if (error instanceof NumberFormatException) {
                            amountLayoutView.setError(activity.getString(R.string.not_a_number));
                        } else {
                            LoggerHelper.e("ERR", error.getMessage(), error);
                        }
                    })
                    .retry()
                    .subscribe();

            amountView.setOnFocusChangeListener((v, focused) -> {
                if (focused) {
                    DrawableColorHelper.changeColorForDrawable(activity, amountView, R.color.secondary, PorterDuff.Mode.SRC_IN);
                } else {
                    DrawableColorHelper.changeColorForDrawable(activity, amountView, R.color.inactiveInputOutline, PorterDuff.Mode.SRC_IN);
                }
            });
        }
    }


    @Override
    public void onPause() {
        super.onPause();

        subscriptions.dispose();
        subscriptions.clear();
    }

    @OnClick(R.id.list_item_currency_send_btn_send)
    public void onClickSendButton() {
        presenter.onClickSendButton();
    }

    @Override
    public void setTransferIsSupported(boolean value) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) contentContainerView.getLayoutParams();
        if (value) {
            layoutParams.gravity = Gravity.TOP;

            contentContainerView.setLayoutParams(layoutParams);
            supportedView.setVisibility(View.VISIBLE);
            notSupportedView.setVisibility(View.GONE);
            sendButtonView.setVisibility(View.VISIBLE);
        } else {
            layoutParams.gravity = Gravity.CENTER;

            contentContainerView.setLayoutParams(layoutParams);
            supportedView.setVisibility(View.GONE);
            notSupportedView.setVisibility(View.VISIBLE);
            sendButtonView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setRecipientAddress(String address) {
        recipientAddressView.setText(address);
    }

    @Override
    public void setFee(BigDecimal fee, String currencyAbbr) {
        String feeString = String.format(Locale.ENGLISH, "%s %s", fee, currencyAbbr);
        feeView.setText(feeString);
    }

    @Override
    public void setCurrentBalance(BigDecimal balance, String currencyAbbr) {
        String pattern = getString(R.string.list_item_currency_send_amount_hint);
        String hint = String.format(Locale.ENGLISH, pattern, balance, currencyAbbr);
        amountLayoutView.setHint(hint);
    }

    @Override
    public void setReminder(BigDecimal reminder, String currencyAbbr) {
        String pattern = getString(R.string.list_item_currency_send_reminder_hint);
        String hint = String.format(Locale.ENGLISH, pattern, reminder, currencyAbbr);
        reminderView.setText(hint);

        FragmentActivity activity = getActivity();
        if (activity != null) {
            if (reminder.compareTo(BigDecimal.ZERO) < 0) {
                reminderView.setTextColor(ContextCompat.getColor(activity, R.color.error));
            } else {
                reminderView.setTextColor(ContextCompat.getColor(activity, R.color.colorSuccess));
            }
        }

    }

    @Override
    public void setTotalAmount(BigDecimal totalAmount, String currencyAbbr) {
        String totalAmountString = String.format(Locale.ENGLISH, "%s %s", totalAmount, currencyAbbr);
        totalAmountView.setText(totalAmountString);
    }

    @Override
    public void setRecipientName(String name) {
        String pattern = getString(R.string.list_item_currency_send_address_hint);
        String hint = String.format(Locale.ENGLISH, pattern, name);
        recipientAddressLayoutView.setHint(hint);
    }

    @Override
    public void lockSendButton() {
        sendButtonView.setEnabled(false);
    }

    @Override
    public void unlockSendButton() {
        sendButtonView.setEnabled(true);
    }

    @Override
    public void setEditTextIcons(int resourceId) {
        FragmentActivity activity = getActivity();
        if (activity != null) {

            Drawable drawable = getIcon(activity, resourceId);
            feeView.setCompoundDrawablesRelative(drawable, null, null, null);

            drawable = getIcon(activity, resourceId);
            amountView.setCompoundDrawablesRelative(drawable, null, null, null);

            drawable = getIcon(activity, resourceId);
            totalAmountView.setCompoundDrawablesRelative(drawable, null, null, null);

            DrawableColorHelper.changeColorForDrawable(activity, feeView, R.color.inactiveInputOutline, PorterDuff.Mode.SRC_IN);
            DrawableColorHelper.changeColorForDrawable(activity, amountView, R.color.inactiveInputOutline, PorterDuff.Mode.SRC_IN);
            DrawableColorHelper.changeColorForDrawable(activity, totalAmountView, R.color.inactiveInputOutline, PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public void hideCommentField() {
        commentLayoutView.setVisibility(View.GONE);
    }

    @Override
    public void showCommentField() {
        commentLayoutView.setVisibility(View.VISIBLE);
    }

    private Drawable getIcon(Context context, int resourceId) {
        Drawable drawable = ContextCompat.getDrawable(context, resourceId);
        if (drawable == null) {return null;}
        int h = drawable.getIntrinsicHeight();
        int w = drawable.getIntrinsicWidth();
        drawable.setBounds( 0, 0, w, h );

        return drawable;
    }
}
