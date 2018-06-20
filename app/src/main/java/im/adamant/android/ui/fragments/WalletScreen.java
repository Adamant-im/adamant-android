package im.adamant.android.ui.fragments;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import ru.terrakok.cicerone.NavigatorHolder;

import static android.content.Context.CLIPBOARD_SERVICE;

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
    @BindView(R.id.fragment_wallet_cl_free_tokens) View freeTokenButton;
    @BindView(R.id.fragment_wallet_cl_adamant_id) View copyAdamantAddressButton;

    public WalletScreen() {
        // Required empty public constructor
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_wallet_screen;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        freeTokenButton.setOnClickListener((v) -> {
            presenter.onClickGetFreeTokenButton();
        });

        //Do not use the presenter in order to avoid duplication of operations when switching fragments.
        copyAdamantAddressButton.setOnClickListener((v) -> {
            Activity activity = getActivity();
            if (activity != null){
                ClipData clip = ClipData.newPlainText("address", adamantAddressView.getText().toString());
                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);

                if(clipboard != null){
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(activity.getApplicationContext(), R.string.address_was_copied, Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    @Override
    public void displayAdamantAddress(String address) {
        adamantAddressView.setText(address);
    }

    @Override
    public void displayAdamantBalance(BigDecimal balance) {
        adamantBalanceView.setText(String.format(Locale.ENGLISH, "%.3f", balance));
    }

    @Override
    public void displayFreeTokenPageButton() {
        freeTokenButton.setVisibility(View.VISIBLE);
    }
    
}
