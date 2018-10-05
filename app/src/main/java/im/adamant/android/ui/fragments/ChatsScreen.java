package im.adamant.android.ui.fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import im.adamant.android.R;
import im.adamant.android.presenters.ChatsPresenter;
import im.adamant.android.ui.adapters.ChatsAdapter;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.mvp_view.ChatsView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsScreen extends BaseFragment implements ChatsView, ChatsAdapter.SelectItemListener {
    @Inject
    Provider<ChatsPresenter> presenterProvider;

    @Inject
    ChatsAdapter adapter;

    //--Moxy
    @InjectPresenter
    ChatsPresenter presenter;

    @ProvidePresenter
    public ChatsPresenter getPresenter(){
        return presenterProvider.get();
    }

    //--ButterKnife
    @BindView(R.id.activity_chats_rv_chats)
    RecyclerView chatList;
    @BindView(R.id.shimmer_view_container)
    ShimmerFrameLayout shimmer;

    public ChatsScreen() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_chats_screen;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        chatList.setLayoutManager(layoutManager);
        chatList.setAdapter(adapter);

        adapter.setListener(this);

        return view;
    }

    @Override
    public void showChats(List<Chat> chats) {
        adapter.updateDataset(chats);
        shimmer.stopShimmerAnimation();
        chatList.setVisibility(View.VISIBLE);
        shimmer.setVisibility(View.GONE);
    }


    @Override
    public void onResume() {
        super.onResume();
        chatList.setVisibility(View.GONE);
        shimmer.setVisibility(View.VISIBLE);
        shimmer.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        super.onPause();
        shimmer.stopShimmerAnimation();
        chatList.setVisibility(View.VISIBLE);
        shimmer.setVisibility(View.GONE);
    }

    @Override
    public void itemWasSelected(Chat chat) {
        presenter.onChatWasSelected(chat);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_chats_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_create_new_chat:
                presenter.onClickCreateNewChatButton();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
