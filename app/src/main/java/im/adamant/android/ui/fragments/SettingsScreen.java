package im.adamant.android.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import butterknife.OnClick;
import im.adamant.android.AdamantApplication;
import im.adamant.android.BuildConfig;
import im.adamant.android.R;
import im.adamant.android.presenters.SettingsPresenter;
import im.adamant.android.ui.adapters.ServerNodeAdapter;
import im.adamant.android.core.entities.ServerNode;
import im.adamant.android.ui.mvp_view.SettingsView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsScreen extends BaseFragment implements SettingsView {

    @Inject
    ServerNodeAdapter adapter;

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
        nodeListView.setAdapter(adapter);

        newNodeAddressView.setOnFocusChangeListener( (edittextView, isFocused) -> {
            if (!isFocused){
                hideKeyboard();
            }
        });

        return view;
    }

    @OnClick(R.id.fragment_settings_btn_add_new_node)
    public void onClickAddNewNode() {
        presenter.onClickAddNewNode(newNodeAddressView.getText().toString());
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
