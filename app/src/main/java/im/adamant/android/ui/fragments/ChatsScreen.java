package im.adamant.android.ui.fragments;


import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import im.adamant.android.R;
import im.adamant.android.helpers.AnimationUtils;
import im.adamant.android.ui.adapters.ChatsAdapter;
import im.adamant.android.ui.custom_view.EndlessRecyclerViewScrollListener;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.fragments.base.BaseFragment;
import im.adamant.android.ui.mvp_view.ChatsView;
import im.adamant.android.ui.presenters.ChatsPresenter;

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
    public ChatsPresenter getPresenter() {
        return presenterProvider.get();
    }

    //--ButterKnife
    @BindView(R.id.fragment_chats_rv_chats)
    RecyclerView chatList;

    @BindView(R.id.fragment_chats_pb_progress)
    ProgressBar progressBar;

    @BindView(R.id.fragment_chats_fab_add_chat)
    FloatingActionButton addChatFab;

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

    private LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        layoutManager = new LinearLayoutManager(this.getContext());
        chatList.setLayoutManager(layoutManager);
        chatList.setAdapter(adapter);

        Drawable divider = ContextCompat.getDrawable(chatList.getContext(), R.drawable.line_divider);
        int avatarWidth = getResources().getDimensionPixelSize(R.dimen.list_item_avatar_size);
        int itemPadding = getResources().getDimensionPixelSize(R.dimen.list_item_chat_padding);
        InsetDrawable insetDivider = new InsetDrawable(divider, avatarWidth + (itemPadding * 2), 0, 0, 0);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(chatList.getContext(), layoutManager.getOrientation());
        itemDecoration.setDrawable(insetDivider);
        chatList.addItemDecoration(itemDecoration);

        adapter.setListener(this);

        chatList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && addChatFab.getVisibility() == View.VISIBLE) {
                    addChatFab.hide();
                } else if (dy < 0 && addChatFab.getVisibility() != View.VISIBLE) {
                    addChatFab.show();
                }
            }
        });

        addChatFab.setOnClickListener(v -> presenter.onClickCreateNewChatButton(buildRevealSettings()));

        return view;
    }

    @Override
    public void showChats(List<Chat> chats) {
        adapter.updateDataset(chats);
        if (endlessRecyclerViewScrollListener != null) {
            endlessRecyclerViewScrollListener.onScrolled(chatList, 0, 0); //Invalidate for endless scroll
        }
    }

    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    @Override
    public void onResume() {
        super.onResume();
        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                presenter.onLoadMore();
            }
        };
        endlessRecyclerViewScrollListener.setVisibleThreshold(12);
        chatList.addOnScrollListener(endlessRecyclerViewScrollListener);
    }

    @Override
    public void progress(boolean value) {
        if (endlessRecyclerViewScrollListener != null) {
            endlessRecyclerViewScrollListener.setLoading(value);
        }
        if (value) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void itemWasSelected(Chat chat) {
        presenter.onChatWasSelected(chat);
    }

    private AnimationUtils.RevealAnimationSetting buildRevealSettings() {
        if (getActivity() != null) {
            View containerView = getActivity().findViewById(R.id.main_screen_content);
            return AnimationUtils.RevealAnimationSetting.with(
                    (int) (addChatFab.getX() + addChatFab.getWidth() / 2),
                    (int) (addChatFab.getY() + addChatFab.getHeight() / 2),
                    containerView.getWidth(),
                    containerView.getHeight());
        } else {
            return null;
        }

    }

}
