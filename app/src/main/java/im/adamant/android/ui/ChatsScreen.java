package im.adamant.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.presenters.ChatsPresenter;
import im.adamant.android.ui.adapters.ChatsAdapter;
import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.mvp_view.ChatsView;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import dagger.android.AndroidInjection;
import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.commands.Command;
import ru.terrakok.cicerone.commands.Forward;
import ru.terrakok.cicerone.commands.SystemMessage;

public class ChatsScreen extends BaseActivity implements ChatsView, ChatsAdapter.SelectItemListener {

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
    @BindView(R.id.activity_chats_rv_chats) RecyclerView chatList;
    @BindView(R.id.shimmer_view_container)  ShimmerFrameLayout shimmer;


    //--Activity
    @Override
    public int getLayoutId() {
        return R.layout.activity_chats_screen;
    }

    @Override
    public boolean withBackButton() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        chatList.setVisibility(View.GONE);
        shimmer.setVisibility(View.VISIBLE);
        shimmer.startShimmerAnimation();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        chatList.setLayoutManager(layoutManager);
        chatList.setAdapter(adapter);
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

    @Override
    protected void onResume() {
        super.onResume();
        navigatorHolder.setNavigator(navigator);
    }

    @Override
    protected void onPause() {
        super.onPause();
        navigatorHolder.removeNavigator();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_chats_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_create_new_chat : {
                presenter.onClickCreateNewChatButton();
            }
        }

        return true;
    }

    private Navigator navigator = new Navigator() {
        @Override
        public void applyCommands(Command[] commands) {
            for (Command command : commands){
                apply(command);
            }
        }

        private void apply(Command command){
            if (command instanceof Forward) {
                Forward forward = (Forward)command;
                switch (forward.getScreenKey()){
                    case Screens.MESSAGES_SCREEN: {
                        Intent intent = new Intent(getApplicationContext(), MessagesScreen.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(MessagesScreen.ARG_CHAT, (Serializable) forward.getTransitionData());
                        intent.putExtras(bundle);

                        startActivity(intent);
                    }
                    break;

                    case Screens.CREATE_CHAT_SCREEN: {
                        Intent intent = new Intent(getApplicationContext(), CreateChatScreen.class);
                        startActivity(intent);
                    }
                    break;
                }
            } else if(command instanceof SystemMessage){
                SystemMessage message = (SystemMessage) command;
                Toast.makeText(getApplicationContext(), message.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    };
}
