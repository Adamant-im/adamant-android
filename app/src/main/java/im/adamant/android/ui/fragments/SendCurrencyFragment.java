package im.adamant.android.ui.fragments;

import android.graphics.PorterDuff;
import android.os.Bundle;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import javax.inject.Inject;
import javax.inject.Provider;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import butterknife.BindView;
import im.adamant.android.R;
import im.adamant.android.helpers.DrawableColorHeleper;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
import im.adamant.android.ui.mvp_view.SendCurrencyTransferView;
import im.adamant.android.ui.presenters.SendCurrencyPresenter;

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

    @BindView(R.id.list_item_currency_amount) TextInputEditText amountView;
    @BindView(R.id.list_item_currency_send_il_amount_layout) TextInputLayout amountLayoutView;
    @BindView(R.id.list_item_currency_send_fee) TextInputEditText feeView;
    @BindView(R.id.list_item_currency_send_il_fee_layout) TextInputLayout feeLayoutView;

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
            DrawableColorHeleper.changeColorForDrawable(activity, feeView, R.color.inactiveInputOutline, PorterDuff.Mode.SRC_IN);
        }
    }
}
