package im.adamant.android.ui.fragments;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
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
import im.adamant.android.ui.fragments.base.BaseFragment;
import im.adamant.android.ui.presenters.SettingsPresenter;
import im.adamant.android.ui.mvp_view.SettingsView;

/**
 * A simple {@link Fragment} subclass.
 */
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
    @BindView(R.id.fragment_settings_tv_notification) TextView pushNotificationServiceView;
    @BindView(R.id.fragment_settings_btn_change_lang) TextView changeLanguageButtonView;
    @BindView(R.id.fragment_settings_pb_progress) ProgressBar progressBarView;

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

        return view;
    }

    @OnClick(R.id.fragment_settings_tr_show_nodes)
    public void onClickShowNodesList() {
        presenter.onClickShowNodesList();
    }


    @OnClick(R.id.fragment_settings_btn_change_lang)
    public void onSelectLanguage() {
        androidx.appcompat.app.AlertDialog.Builder languageDialogBuilder = getLanguageDialogBuilder(supportedLocales);
        languageDialogBuilder.create().show();
    }

    @Override
    public void setCheckedStoreKeyPairOption(boolean value) {
        storeKeypairView.setChecked(value);
    }

    @Override
    public void setEnableStoreKeyPairOption(boolean value) {
        storeKeypairView.setEnabled(value);
    }

    @OnCheckedChanged(R.id.fragment_settings_sw_store_keypair)
    public void onSwitchStoreKeyPair(CompoundButton button, boolean checked) {
        presenter.onSetCheckedStoreKeypair(checked, false);
    }

    @OnClick(R.id.fragment_settings_tv_notification)
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
    public void showTEENotSupportedDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = null;
        FragmentActivity activity = getActivity();

        if (activity != null) {
            builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
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

    //TODO: Refactor: method to long and dirty
    private androidx.appcompat.app.AlertDialog.Builder getLanguageDialogBuilder(List<Locale> supportedLocales) {
        androidx.appcompat.app.AlertDialog.Builder builder = null;
        FragmentActivity activity = getActivity();

        if (activity != null){
            builder = new androidx.appcompat.app.AlertDialog.Builder(activity);

            builder.setTitle(getString(R.string.fragment_settings_choose_language));

            CharSequence[] titles = new CharSequence[supportedLocales.size()];

            Locale locale = LocaleChanger.getLocale();
            int defaultSelected = 0;
            for (int i = 0; i < titles.length; i++){
                titles[i] = supportedLocales.get(i).getDisplayName();

                if (locale.equals(supportedLocales.get(i))){
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
                    LocaleChanger.setLocale(supportedLocales.get(currentSelected));
                    activity.recreate();
                }
            });
            builder.setNegativeButton(R.string.no, null);
        }


        return builder;
    }
}
