package im.adamant.android.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import java.math.BigDecimal;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import im.adamant.android.R;
import im.adamant.android.presenters.WalletPresenter;
import im.adamant.android.ui.mvp_view.WalletView;

/**
 * A simple {@link Fragment} subclass.
 */
public class WalletScreen extends BaseFragment implements WalletView {

    @Inject
    Provider<WalletPresenter> presenterProvider;

    //--Moxy
    @InjectPresenter
    WalletPresenter presenter;

    @ProvidePresenter
    public WalletPresenter getPresenter(){
        return presenterProvider.get();
    }

    @BindView(R.id.fragment_wallet_tv_adamant_id) TextView adamantAddressView;
    @BindView(R.id.fragment_wallet_tv_adamant_balance) TextView adamantBalanceView;

    public WalletScreen() {
        // Required empty public constructor
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_wallet_screen;
    }

    @Override
    public void displayAdamantAddress(String address) {
        adamantAddressView.setText(address);
    }

    @Override
    public void displayAdamantBalance(BigDecimal balance) {
        adamantBalanceView.setText(String.format(Locale.ENGLISH, "%.3f", balance));
    }
}
