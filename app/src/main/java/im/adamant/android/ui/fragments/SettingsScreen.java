package im.adamant.android.ui.fragments;


import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.franmontiel.localechanger.LocaleChanger;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import im.adamant.android.BuildConfig;
import im.adamant.android.R;
import im.adamant.android.interactors.push.PushNotificationServiceFacade;
import im.adamant.android.interactors.push.SupportedPushNotificationFacadeType;
import im.adamant.android.ui.fragments.base.BaseFragment;
import im.adamant.android.ui.presenters.SettingsPresenter;
import im.adamant.android.ui.mvp_view.SettingsView;

public class SettingsScreen extends BaseFragment implements SettingsView {

    @Inject
    Provider<SettingsPresenter> presenterProvider;

    @Inject
    List<Locale> supportedLocales;

    //--Moxy
    @InjectPresenter
    SettingsPresenter presenter;

    @ProvidePresenter
    public SettingsPresenter getPresenter(){
        return presenterProvider.get();
    }

    @BindView(R.id.fragment_settings_tv_version) TextView versionView;
    @BindView(R.id.fragment_settings_sw_store_keypair) Switch storeKeypairView;
    @BindView(R.id.fragment_settings_tr_store_keypair) TableRow storeKeypairTableRowView;
    @BindView(R.id.fragment_settings_tv_notification) TextView pushNotificationServiceView;
    @BindView(R.id.fragment_settings_btn_change_lang) TextView changeLanguageButtonView;
    @BindView(R.id.fragment_settings_tr_change_lang) TableRow changeLanguageTableRowView;
    @BindView(R.id.fragment_settings_pb_progress) ProgressBar progressBarView;
    @BindView(R.id.fragment_settings_tr_subscribe_to_push) TableRow pushNotificationServiceLayoutView;

    public SettingsScreen() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_settings_screen;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        String versionText = String.format(Locale.ENGLISH, getString(R.string.fragment_settings_version), BuildConfig.VERSION_NAME);
        versionView.setText(versionText);

        Locale locale = LocaleChanger.getLocale();
        changeLanguageButtonView.setText(locale.getDisplayLanguage());

        changeLanguageButtonView.setPaintFlags(changeLanguageButtonView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        pushNotificationServiceView.setPaintFlags(pushNotificationServiceView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        return view;
    }

    @OnClick(R.id.fragment_settings_tr_show_nodes)
    public void onClickShowNodesList() {
        presenter.onClickShowNodesList();
    }


    @OnClick(R.id.fragment_settings_tr_change_lang)
    public void onSelectLanguage() {
        AlertDialog.Builder languageDialogBuilder = getLanguageDialogBuilder(supportedLocales);
        languageDialogBuilder.create().show();
    }

    @OnClick(R.id.fragment_settings_tr_exit)
    public void onClickExit() {
        presenter.onClickShowExitDialogButton();
    }

    @Override
    public void setCheckedStoreKeyPairOption(boolean value) {
        storeKeypairView.setChecked(value);
    }

    @Override
    public void setEnableStoreKeyPairOption(boolean value) {
        storeKeypairView.setEnabled(value);
    }

    @OnClick(R.id.fragment_settings_tr_store_keypair)
    public void onClickStoreKeyPair() {
        storeKeypairView.setChecked(!storeKeypairView.isChecked());
    }

    @OnCheckedChanged(R.id.fragment_settings_sw_store_keypair)
    public void onSwitchStoreKeyPair(CompoundButton button, boolean checked) {
        presenter.onSetCheckedStoreKeypair(checked, false);
    }

    @OnClick(R.id.fragment_settings_tr_subscribe_to_push)
    public void onClickSelectPushNotificationService() {
        presenter.onClickShowSelectPushService();
    }

    @Override
    public void setEnablePushOption(boolean value) {
        pushNotificationServiceView.setEnabled(value);
    }

    @Override
    public void displayCurrentNotificationFacade(PushNotificationServiceFacade facade) {
        pushNotificationServiceView.setText(getString(facade.getShortTitleResource()));
    }

    @Override
    public void startProgress() {
        progressBarView.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopProgress() {
        progressBarView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setEnablePushServiceTypeOption(boolean value) {

    }

    @Override
    public void showExitDialog() {
        Activity activity = getActivity();
        if (activity != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder
                    .setTitle(R.string.dialog_logout_title)
                    .setMessage(R.string.dialog_logout_message)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        presenter.onClickExitButton();
                    })
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> {})
                    .show();
        }
    }

    @Override
    public void showTEENotSupportedDialog() {
        AlertDialog.Builder builder = null;
        FragmentActivity activity = getActivity();

        if (activity != null) {
            builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.warning);
            builder.setMessage(R.string.tee_not_supported);
            builder.setNegativeButton(R.string.no, (d, w) -> {
                storeKeypairView.setChecked(false);
                d.dismiss();
            });
            builder.setPositiveButton(R.string.yes, (d, w) -> presenter.onSetCheckedStoreKeypair(true, true));

            builder.show();
        }
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showMessage(int messageResource) {
        Toast.makeText(getActivity(), messageResource, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showSelectServiceDialog(List<PushNotificationServiceFacade> facades, PushNotificationServiceFacade current) {
        AlertDialog.Builder builder = null;

        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.activity_push_subscription_select_type_title));

        CharSequence[] titles = new CharSequence[facades.size()];

        int defaultSelected = 0;
        SupportedPushNotificationFacadeType currentFacadeType = current.getFacadeType();
        for (int i = 0; i < titles.length; i++){
            titles[i] = getString(facades.get(i).getTitleResource());

            if (currentFacadeType.equals(facades.get(i).getFacadeType())){
                defaultSelected = i;
            }
        }

        AtomicInteger selectedLangIndex = new AtomicInteger(defaultSelected);

        builder.setSingleChoiceItems(titles, defaultSelected, (d, i) -> {
            selectedLangIndex.set(i);
        });

        int finalDefaultSelected = defaultSelected;
        builder.setPositiveButton(R.string.yes, (d, i) -> {
            int currentSelected = selectedLangIndex.get();
            if (finalDefaultSelected != currentSelected){
                presenter.onClickSetNewPushService(facades.get(currentSelected));
            }
        });
        builder.setNegativeButton(R.string.no, null);

        builder.show();
    }

    private AlertDialog.Builder getLanguageDialogBuilder(List<Locale> supportedLocales) {
        AlertDialog.Builder builder = null;
        FragmentActivity activity = getActivity();

        if (activity != null){
            builder = new AlertDialog.Builder(activity);

            CharSequence[] titles = new CharSequence[supportedLocales.size()];

            Locale locale = LocaleChanger.getLocale();
            int defaultSelected = 0;
            for (int i = 0; i < titles.length; i++){
                titles[i] = supportedLocales.get(i).getDisplayName();

                if (locale.equals(supportedLocales.get(i))){
                    defaultSelected = i;
                }
            }

            int finalDefaultSelected = defaultSelected;
            builder.setSingleChoiceItems(titles, defaultSelected, (d, i) -> {
                if (finalDefaultSelected != i){
                    d.dismiss();
                    LocaleChanger.setLocale(supportedLocales.get(i));
                    activity.recreate();
                }
            });

        }

        return builder;
    }
}
