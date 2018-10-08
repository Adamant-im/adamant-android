package im.adamant.android.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;
import im.adamant.android.R;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.interactors.AccountInteractor;
import im.adamant.android.presenters.MainPresenter;
import ru.terrakok.cicerone.Router;

public class BottomNavigationDrawerFragment extends BottomSheetDialogFragment {

    @Inject
    Router router;

    @Inject
    AdamantApiWrapper api;

    @Inject
    MainPresenter mainPresenter;

    @Inject
    Avatar avatar;

    @Inject
    AccountInteractor accountInteractor;

    @BindView(R.id.fragment_bottom_navigation_iv_avatar)
    ImageView avatarView;

    @BindView(R.id.fragment_bottom_navigation_tv_address)
    TextView addressView;

    @BindView(R.id.fragment_bottom_navigation_tv_balance)
    TextView balanceView;

    @BindView(R.id.fragment_bottom_navigation_nv_menu)
    NavigationView navigationView;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    //TODO: Think about whether there is a presenter and a refactor code
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_navigation, container, false);
        ButterKnife.bind(this, view);

        if (api.isAuthorized()){
            avatar.build(
                        api.getKeyPair().getPublicKeyString().toLowerCase(),
                        (int) getResources().getDimension(R.dimen.fragment_bottom_navigation_avatar_size)
                ).subscribe(bitmap -> {
                    avatarView.setImageBitmap(bitmap);
            });

            addressView.setText(api.getAccount().getAddress());

            accountInteractor
                    .getAdamantBalance()
                    .subscribe(balance -> {
                        balanceView.setText(balance.toString() + " " + getString(R.string.adm_currency_abbr));
                    });
        }

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.navigation_chats: {
                    mainPresenter.onSelectedChatsScreen();
                }
                break;
                case R.id.navigation_wallet: {
                    mainPresenter.onSelectedWalletScreen();
                }
                break;
                case R.id.navigation_settings: {
                    mainPresenter.onSelectedSettingsScreen();
                }
                break;
                case R.id.navigation_exit: {
                    //VERY IMPORTANT: Do not delete the lock code of the button as this will result in a memory leak and crash the application.
                    menuItem.setEnabled(false);
                    Activity activity = getActivity();
                    if (activity != null){
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder
                                .setTitle(R.string.dialog_logout_title)
                                .setMessage(R.string.dialog_logout_message)
                                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                    mainPresenter.onClickExitButton();
                                    menuItem.setEnabled(true);
                                })
                                .setNegativeButton(android.R.string.cancel, (dialog, which) -> menuItem.setEnabled(true))
                                .show();
                    }
                }
                break;
            }

            dismiss();
            return true;
        });

        return view;
    }
}
