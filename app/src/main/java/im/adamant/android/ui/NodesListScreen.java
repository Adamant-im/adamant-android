package im.adamant.android.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.EditText;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import javax.inject.Inject;
import javax.inject.Provider;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import im.adamant.android.AdamantApplication;
import im.adamant.android.R;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.ui.adapters.ServerNodeAdapter;
import im.adamant.android.ui.custom_view.IgnoreLastDividerItemDecorator;
import im.adamant.android.ui.mvp_view.NodesListView;
import im.adamant.android.ui.presenters.NodesListPresenter;
import io.reactivex.disposables.Disposable;

public class NodesListScreen extends BaseActivity implements NodesListView {

    @Inject
    Provider<NodesListPresenter> presenterProvider;

    //--Moxy
    @InjectPresenter
    NodesListPresenter presenter;

    @ProvidePresenter
    public NodesListPresenter getPresenter(){
        return presenterProvider.get();
    }

    @Inject
    ServerNodeAdapter nodeAdapter;

    @BindView(R.id.fragment_settings_rv_list_of_nodes)
    RecyclerView nodeListView;
    @BindView(R.id.fragment_settings_et_new_node_address)
    EditText newNodeAddressView;

    Disposable deleteItemDisposable;
    Disposable switchItemDisposable;

    @Override
    public int getLayoutId() {
        return R.layout.activity_nodes_list_screen;
    }

    @Override
    public boolean withBackButton() {
        return true;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        setTitle(R.string.activity_nodes_list_title);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        nodeListView.setLayoutManager(layoutManager);

        Drawable drawable = ContextCompat.getDrawable(nodeListView.getContext(), R.drawable.line_divider);
        if (drawable != null) {
            IgnoreLastDividerItemDecorator dividerItemDecoration = new IgnoreLastDividerItemDecorator(drawable);
            nodeListView.addItemDecoration(dividerItemDecoration);
        }

        nodeListView.setAdapter(nodeAdapter);

        newNodeAddressView.setOnFocusChangeListener( (edittextView, isFocused) -> {
            if (!isFocused){
                hideKeyboard();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        deleteItemDisposable = nodeAdapter
                .getRemoveObservable()
                .subscribe(serverNode -> {
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
                        builder
                                .setTitle(R.string.warning)
                                .setMessage(R.string.activity_nodes_list_dialog_delete_node)
                                .setPositiveButton(android.R.string.yes, (d,w) -> {
                                    presenter.onClickDeleteNode(serverNode);
                                })
                                .setNegativeButton(android.R.string.no, (d,w) -> {})
                                .show();
                });

        switchItemDisposable = nodeAdapter
                .getSwitchObservable()
                .subscribe(
                        index -> presenter.onClickSwitchNode(index),
                        error -> LoggerHelper.e("SwitchNode", error.getMessage(), error)
                );

        nodeAdapter.startListenChanges();
    }

    @Override
    protected void onPause() {
        nodeAdapter.stopListenChanges();

        if (deleteItemDisposable != null){
            deleteItemDisposable.dispose();
        }
        if (switchItemDisposable != null){
            switchItemDisposable.dispose();
        }
        super.onPause();
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
        AdamantApplication.hideKeyboard(this, newNodeAddressView);
    }
}
