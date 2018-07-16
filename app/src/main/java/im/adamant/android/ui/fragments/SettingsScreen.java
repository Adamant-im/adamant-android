package im.adamant.android.ui.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import butterknife.OnClick;
import im.adamant.android.AdamantApplication;
import im.adamant.android.BuildConfig;
import im.adamant.android.R;
import im.adamant.android.presenters.SettingsPresenter;
import im.adamant.android.ui.adapters.ServerNodeAdapter;
import im.adamant.android.ui.mvp_view.SettingsView;
import io.reactivex.disposables.Disposable;

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

    public SettingsScreen() {
        // Required empty public constructor
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_settings_screen;
    }

    @Override
    public int getActivityTitleId() {
        return R.string.bottom_menu_title_settings;
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
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);
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
    }

    @Override
    public void onPause() {
        super.onPause();
        if (adapterDisposable != null){
            adapterDisposable.dispose();
        }
    }

    @OnClick(R.id.fragment_settings_btn_add_new_node)
    public void onClickAddNewNode() {
        presenter.onClickAddNewNode(newNodeAddressView.getText().toString());
    }
    @OnClick(R.id.fragment_settings_btn_change_lang)
    public void onSelectLanguage() {
//        Activity activity = getActivity();
//        if (activity != null){
//            AdamantApplication.getLanguageSwitcher().showChangeLanguageDialog(getActivity());
//        }

        android.support.v7.app.AlertDialog.Builder languageDialogBuilder = getLanguageDialogBuilder(supportedLocales);
        languageDialogBuilder.create().show();
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

    //TODO: Refactor: method to long and dirty
    private android.support.v7.app.AlertDialog.Builder getLanguageDialogBuilder(List<Locale> supportedLocales) {
        android.support.v7.app.AlertDialog.Builder builder = null;
        FragmentActivity activity = getActivity();

        if (activity != null){
            builder = new android.support.v7.app.AlertDialog.Builder(activity);

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
