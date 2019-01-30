package im.adamant.android.ui.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

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
import butterknife.OnItemSelected;
import im.adamant.android.AdamantApplication;
import im.adamant.android.BuildConfig;
import im.adamant.android.R;
import im.adamant.android.ui.presenters.SettingsPresenter;
import im.adamant.android.services.SaveSettingsService;
import im.adamant.android.ui.mvp_view.SettingsView;
import sm.euzee.github.com.servicemanager.ServiceManager;

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
    @BindView(R.id.fragment_settings_sw_push_notifications) Switch enablePushNotifications;
    @BindView(R.id.fragment_settings_btn_change_lang) TextView changeLanguageButtonView;

    public SettingsScreen() {
        // Required empty public constructor
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_settings_screen;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  super.onCreateView(inflater, container, savedInstanceState);

        String versionText = String.format(Locale.ENGLISH, getString(R.string.fragment_settings_version), BuildConfig.VERSION_NAME);
        versionView.setText(versionText);

        Locale locale = LocaleChanger.getLocale();
        changeLanguageButtonView.setText(locale.getDisplayLanguage());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    //TODO: Maybe unsubscribe when setUserVisibleHint == false. Think about this ;)
    @Override
    public void onPause() {
        presenter.onClickSaveSettings();

        super.onPause();
    }

    @OnClick(R.id.fragment_settings_tr_show_nodes)
    public void onClickShowNodesList() {
        presenter.onClickShowNodesList();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (!isVisibleToUser && (presenter != null)){
            presenter.onClickSaveSettings();
        }
    }


    @OnClick(R.id.fragment_settings_btn_change_lang)
    public void onSelectLanguage() {
        androidx.appcompat.app.AlertDialog.Builder languageDialogBuilder = getLanguageDialogBuilder(supportedLocales);
        languageDialogBuilder.create().show();
    }

    @Override
    public void setStoreKeyPairOption(boolean value) {
        storeKeypairView.setChecked(value);
    }

    @OnCheckedChanged(R.id.fragment_settings_sw_store_keypair)
    public void onSwitchStoreKeyPair(CompoundButton button, boolean checked) {
        presenter.onSwitchStoreKeypair(checked);
    }

    @Override
    public void switchPushOption(boolean value) {
        enablePushNotifications.setChecked(value);
    }

    @Override
    public void enablePushOption(boolean value) {
        enablePushNotifications.setEnabled(value);
        if (!value){
            enablePushNotifications.setChecked(false);
        }
        addressPushService.setEnabled(value);
    }


    @Override
    public void callSaveSettingsService() {
        Activity activity = getActivity();
        if (activity != null) {
            Context context = activity.getApplicationContext();
            Intent intent = new Intent(context, SaveSettingsService.class);
            intent.putExtra(SaveSettingsService.IS_SAVE_KEYPAIR, storeKeypairView.isChecked());
            intent.putExtra(SaveSettingsService.IS_RECEIVE_NOTIFICATIONS, enablePushNotifications.isChecked());

            ServiceManager.runService(context, intent);
        }
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
