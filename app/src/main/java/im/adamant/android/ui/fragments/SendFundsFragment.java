package im.adamant.android.ui.fragments;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.rxbinding3.widget.RxTextView;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Provider;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import butterknife.BindView;
import butterknife.OnClick;
import im.adamant.android.AdamantApplication;
import im.adamant.android.R;
import im.adamant.android.helpers.DrawableColorHelper;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
import im.adamant.android.ui.fragments.base.BaseFragment;
import im.adamant.android.ui.fragments.dialogs.ConfirmationSendFundsDialog;
import im.adamant.android.ui.mvp_view.SendFundsView;
import im.adamant.android.ui.presenters.SendFundsPresenter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class SendFundsFragment extends BaseFragment implements SendFundsView {
    public static final String ARG_WALLET_FACADE_TYPE = "wallet_facade_type";
    public static final String ARG_COMPANION_ID = "companion_id";

    @Inject
    Provider<SendFundsPresenter> presenterProvider;

    //--Moxy
    @InjectPresenter
    SendFundsPresenter presenter;

    @ProvidePresenter
    public SendFundsPresenter getPresenter(){
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

    private boolean isSupportedCurrency = false;

    private AlertDialog alert;


    public static SendFundsFragment newInstance(
            SupportedWalletFacadeType facadeType,
            String companionId
    ) {
        SendFundsFragment pageFragment = new SendFundsFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(ARG_WALLET_FACADE_TYPE, facadeType);
        arguments.putString(ARG_COMPANION_ID, companionId);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_send_funds;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            SupportedWalletFacadeType type = (SupportedWalletFacadeType) arguments.getSerializable(ARG_WALLET_FACADE_TYPE);
            String companionId = arguments.getString(ARG_COMPANION_ID);

            if (type != null) {
                presenter.setCompanionIdAndFacadeType(companionId, type);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        FragmentActivity activity = getActivity();
        if (activity != null){
            Drawable drawable = AppCompatResources.getDrawable(activity, R.drawable.ic_send_address);
            recipientAddressView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        FragmentActivity activity = getActivity();
        if (activity != null){
            DrawableColorHelper.changeColorForDrawable(activity, recipientAddressView, R.color.textMuted, PorterDuff.Mode.SRC_IN);

            RxTextView
                    .textChanges(amountView)
                    .debounce(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                    .doOnNext(presenter::onInputAmount)
                    .doOnError(error -> LoggerHelper.e(getClass().getSimpleName(), error.getMessage(), error))
                    .retry()
                    .subscribe();

            amountView.setOnFocusChangeListener((v, focused) -> {
                if (focused) {
                    DrawableColorHelper.changeColorForDrawable(activity, amountView, R.color.secondary, PorterDuff.Mode.SRC_IN);
                } else {
                    DrawableColorHelper.changeColorForDrawable(activity, amountView, R.color.textMuted, PorterDuff.Mode.SRC_IN);
                }
            });

            RxTextView.textChanges(recipientAddressView)
                    .debounce(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                    .map(CharSequence::toString)
                    .doOnNext(address -> {
                        Editable text = amountView.getText();
                        if (text == null) { return; }

                        presenter.onInputRecipientAddress(address, text.toString());

                    })
                    .doOnError(error -> LoggerHelper.e(getClass().getSimpleName(), error.getMessage(), error))
                    .retry()
                    .subscribe();

        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (alert != null){
            alert.dismiss();
        }

        subscriptions.dispose();
        subscriptions.clear();
    }

    @OnClick(R.id.list_item_currency_send_btn_send)
    public void onClickSendButton() {
        presenter.onClickSendButton();
    }

    @Override
    public void setFundsSendingIsSupported(boolean value) {
        isSupportedCurrency = value;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) contentContainerView.getLayoutParams();
        if (value) {
            layoutParams.gravity = Gravity.TOP;

            contentContainerView.setLayoutParams(layoutParams);
            supportedView.setVisibility(View.VISIBLE);
            notSupportedView.setVisibility(View.GONE);
            sendButtonView.setVisibility(View.VISIBLE);

            show(true);

        } else {
            layoutParams.gravity = Gravity.CENTER;

            contentContainerView.setLayoutParams(layoutParams);
            supportedView.setVisibility(View.GONE);
            notSupportedView.setVisibility(View.VISIBLE);
            sendButtonView.setVisibility(View.GONE);

            show(false);
        }
    }

    private void show(boolean value) {
        if (value && isSupportedCurrency) {
            if (getUserVisibleHint() && amountView != null) {
                amountView.requestFocus();
                AdamantApplication.showKeyboard(Objects.requireNonNull(getActivity()), amountView, 0);
            }
        } else {
            if (amountView != null) {
                AdamantApplication.hideKeyboard(Objects.requireNonNull(getActivity()), amountView);
            }
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        show(isVisibleToUser);
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
    public void lockRecipientAddress() {
        recipientAddressView.setEnabled(false);
    }

    @Override
    public void unlockRecipientAddress() {
        recipientAddressView.setEnabled(true);
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
    public void setEditTextCurrencyIcons(int resourceId) {
        FragmentActivity activity = getActivity();
        if (activity != null) {

            Drawable drawable = getIcon(activity, resourceId);
            feeView.setCompoundDrawablesRelative(drawable, null, null, null);

            drawable = getIcon(activity, resourceId);
            amountView.setCompoundDrawablesRelative(drawable, null, null, null);

            drawable = getIcon(activity, resourceId);
            totalAmountView.setCompoundDrawablesRelative(drawable, null, null, null);

            DrawableColorHelper.changeColorForDrawable(activity, feeView, R.color.textMuted, PorterDuff.Mode.SRC_IN);
            DrawableColorHelper.changeColorForDrawable(activity, amountView, R.color.textMuted, PorterDuff.Mode.SRC_IN);
            DrawableColorHelper.changeColorForDrawable(activity, totalAmountView, R.color.textMuted, PorterDuff.Mode.SRC_IN);
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

    @Override
    public void showRecipientAddressError(int resourceId) {
        recipientAddressLayoutView.setError(getString(resourceId));
    }

    @Override
    public void dropRecipientAddressError() {
        recipientAddressLayoutView.setError("");
    }

    @Override
    public void showAmountError(int resourceId) {
        amountLayoutView.setError(getString(resourceId));
    }

    @Override
    public void dropAmountError() {
        amountLayoutView.setError("");
    }

    @Override
    public void showTransferConfirmationDialog(BigDecimal amount, String currencyAbbr, String address) {
        FragmentActivity activity = getActivity();
        if (activity != null){
            String pattern = getString(R.string.activity_send_funds_dialog_funds_message);
            String message = String.format(Locale.ENGLISH, pattern, amount, currencyAbbr, address);

            ConfirmationSendFundsDialog fragment = ConfirmationSendFundsDialog.provide(presenter, message);
            fragment.show(activity.getSupportFragmentManager(), "transferConfirmation");
        }
    }

    private Drawable getIcon(Context context, int resourceId) {
        Drawable drawable = AppCompatResources.getDrawable(context, resourceId);
        if (drawable == null) {return null;}
        int h = drawable.getIntrinsicHeight();
        int w = drawable.getIntrinsicWidth();
        drawable.setBounds( 0, 0, w, h );

        return drawable;
    }
}
