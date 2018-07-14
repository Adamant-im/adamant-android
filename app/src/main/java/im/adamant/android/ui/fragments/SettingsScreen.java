package im.adamant.android.ui.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import im.adamant.android.AdamantApplication;
import im.adamant.android.BuildConfig;
import im.adamant.android.R;
import im.adamant.android.presenters.SettingsPresenter;
import im.adamant.android.ui.BaseActivity;
import im.adamant.android.ui.adapters.LanguageAdapter;
import im.adamant.android.ui.adapters.ServerNodeAdapter;
import im.adamant.android.ui.mvp_view.SettingsView;
import io.paperdb.Paper;
import io.reactivex.disposables.Disposable;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsScreen extends BaseFragment implements SettingsView {

    @Inject
    ServerNodeAdapter nodeAdapter;

    @Inject
    LanguageAdapter languageAdapter;

    Disposable adapterDisposable;

    @Inject
    Provider<SettingsPresenter> presenterProvider;

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
    @BindView(R.id.fragment_settings_sp_lang_selector) Spinner languageSelector;

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

        languageSelector.setAdapter(languageAdapter);

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
    @OnItemSelected(R.id.fragment_settings_sp_lang_selector)
    public void onSelectLanguage(int i) {
        Paper.book().write("language", languageAdapter.getItem(i).getLanguage());
//        TODO: Refactor
        ((BaseActivity)getActivity()).updateView(Paper.book().read("language"));

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
}
