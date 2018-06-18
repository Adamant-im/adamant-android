package im.adamant.android.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import dagger.android.AndroidInjection;
import dagger.android.support.AndroidSupportInjection;
import im.adamant.android.R;
import im.adamant.android.presenters.ChatsPresenter;
import im.adamant.android.ui.adapters.ChatsAdapter;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.mvp_view.ChatsView;
import ru.terrakok.cicerone.NavigatorHolder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsScreen extends BaseFragment implements ChatsView, ChatsAdapter.SelectItemListener {
    @Inject
    NavigatorHolder navigatorHolder;

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
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_chats_screen;
    }

    @Override
    public String getTitle() {
        return getString(R.string.bottom_menu_title_chats);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        chatList.setVisibility(View.GONE);
        shimmer.setVisibility(View.VISIBLE);
        shimmer.startShimmerAnimation();

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
    public void itemWasSelected(Chat chat) {
        presenter.onChatWasSelected(chat);
    }
}
