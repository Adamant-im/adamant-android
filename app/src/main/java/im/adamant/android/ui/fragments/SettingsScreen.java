package im.adamant.android.ui.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.franmontiel.localechanger.LocaleChanger;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import im.adamant.android.AdamantApplication;
import im.adamant.android.BuildConfig;
import im.adamant.android.Constants;
import im.adamant.android.R;
import im.adamant.android.presenters.PincodePresenter;
import im.adamant.android.presenters.SettingsPresenter;
import im.adamant.android.services.SaveSettingsService;
import im.adamant.android.ui.PinCodeScreen;
import im.adamant.android.ui.adapters.ServerNodeAdapter;
import im.adamant.android.ui.mvp_view.SettingsView;
import io.reactivex.disposables.Disposable;
import sm.euzee.github.com.servicemanager.ServiceManager;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static im.adamant.android.ui.PinCodeScreen.ARG_MODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsScreen extends BaseFragment implements SettingsView {

    @Inject
    ServerNodeAdapter nodeAdapter;

    Disposable adapterDisposable;

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
    @BindView(R.id.fragment_settings_rv_list_of_nodes) RecyclerView nodeListView;
    @BindView(R.id.fragment_settings_et_new_node_address) EditText newNodeAddressView;
    @BindView(R.id.fragment_settings_sw_store_keypair) Switch storeKeypairView;
    @BindView(R.id.fragment_settings_sw_push_notifications) Switch enablePushNotifications;
    @BindView(R.id.fragment_settings_et_push_service_address) EditText addressPushService;

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

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        nodeListView.setLayoutManager(layoutManager);
        nodeListView.setAdapter(nodeAdapter);

        newNodeAddressView.setOnFocusChangeListener( (edittextView, isFocused) -> {
            if (!isFocused){
                hideKeyboard();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapterDisposable = nodeAdapter
                .getRemoveObservable()
                .subscribe(serverNode -> {
                    Activity activity = getActivity();
                    if (activity != null){
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
                        builder
                                .setTitle(R.string.warning)
                                .setMessage(R.string.fragment_settings_dialog_delete_node)
                                .setPositiveButton(android.R.string.yes, (d,w) -> {
                                    presenter.onClickDeleteNode(serverNode);
                                })
                                .setNegativeButton(android.R.string.no, (d,w) -> {})
                                .show();
                    }
                });

        nodeAdapter.startListenChanges();
    }

    //TODO: Maybe unsubscribe when setUserVisibleHint == false. Think about this ;)
    @Override
    public void onPause() {
        saveAllSettings();
        nodeAdapter.stopListenChanges();

        if (adapterDisposable != null){
            adapterDisposable.dispose();
        }

        super.onPause();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (!isVisibleToUser && (presenter != null)){
            saveAllSettings();
        }
    }

    @OnClick(R.id.fragment_settings_btn_add_new_node)
    public void onClickAddNewNode() {
        presenter.onClickAddNewNode(newNodeAddressView.getText().toString());
    }
    @OnClick(R.id.fragment_settings_btn_change_lang)
    public void onSelectLanguage() {
        androidx.appcompat.app.AlertDialog.Builder languageDialogBuilder = getLanguageDialogBuilder(supportedLocales);
        languageDialogBuilder.create().show();
    }

    @OnCheckedChanged(R.id.fragment_settings_sw_store_keypair)
    public void onSelectedSaveKeypair(CompoundButton button, boolean checked) {
        Context applicationContext = Objects.requireNonNull(getActivity()).getApplicationContext();
        if (applicationContext == null) {return;}

        if (checked) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(ARG_MODE, PincodePresenter.Mode.ENCRYPT_KEYPAIR);

            Intent intent = new Intent(applicationContext, PinCodeScreen.class);
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            intent.putExtras(bundle);

            this.startActivityForResult(intent, Constants.PINCODE_WAS_ENTERED);
        } else {
            presenter.onClickDropSavedKeyPair();
        }
    }

    @Override
    public void clearNodeTextField() {
        newNodeAddressView.setText("");
    }

    @Override
    public void hideKeyboard() {
        if (getActivity() != null){
            AdamantApplication.hideKeyboard(getActivity(), newNodeAddressView);
        }
    }

    @Override
    public void setStoreKeyPairOption(boolean value) {
        storeKeypairView.setChecked(value);
    }

    @Override
    public void setEnablePushOption(boolean value) {
        enablePushNotifications.setChecked(value);
    }

    @Override
    public void setAddressPushService(String address) {
        addressPushService.setText(address);
    }



    private void saveAllSettings() {
        if (presenter != null) {
            presenter.onClickSaveSettings(enablePushNotifications.isChecked(), addressPushService.getText().toString());
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
