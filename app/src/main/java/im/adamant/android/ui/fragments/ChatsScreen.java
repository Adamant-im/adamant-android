package im.adamant.android.ui.fragments;


import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import im.adamant.android.R;
import im.adamant.android.ui.fragments.base.BaseFragment;
import im.adamant.android.ui.presenters.ChatsPresenter;
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
    @BindView(R.id.fragment_chats_rv_chats)
    RecyclerView chatList;

    @BindView(R.id.fragment_chats_pb_progress)
    ProgressBar progressBar;

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

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
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

        return view;
    }

    @Override
    public void showChats(List<Chat> chats) {
        adapter.updateDataset(chats);
    }

    @Override
    public void progress(boolean value) {
        if (value) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
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
