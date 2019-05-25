package im.adamant.android.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.google.android.material.bottomappbar.BottomAppBar;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import im.adamant.android.Constants;
import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.avatars.Avatar;
import im.adamant.android.ui.fragments.BottomCreateChatFragment;
import im.adamant.android.ui.navigators.DefaultNavigator;
import im.adamant.android.ui.presenters.MainPresenter;
import im.adamant.android.ui.fragments.BottomNavigationDrawerFragment;
import im.adamant.android.ui.fragments.ChatsScreen;
import im.adamant.android.ui.fragments.SettingsScreen;
import im.adamant.android.ui.fragments.WalletScreen;
import im.adamant.android.ui.mvp_view.MainView;
import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.commands.Back;
import ru.terrakok.cicerone.commands.BackTo;
import ru.terrakok.cicerone.commands.Command;
import ru.terrakok.cicerone.commands.Forward;
import ru.terrakok.cicerone.commands.Replace;
import ru.terrakok.cicerone.commands.SystemMessage;


public class MainScreen extends BaseActivity implements MainView, HasSupportFragmentInjector {
    public static final String ARG_CURRENT_SCREEN = "current_screen";

    @Inject
    NavigatorHolder navigatorHolder;

    @Inject
    Provider<MainPresenter> presenterProvider;

    //--Moxy
    @InjectPresenter
    MainPresenter presenter;

    @ProvidePresenter
    public MainPresenter getPresenter(){
        return presenterProvider.get();
    }

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;


    @BindView(R.id.main_screen_content) FrameLayout content;

    @BindView(R.id.bottom_appbar)
    BottomAppBar appBar;

    @Inject
    Avatar avatar;

    private ChatsScreen chatsScreen;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public boolean withBackButton() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        setSupportActionBar(appBar);
    }

    //TODO: Don't recreate fragments

    @Override
    public void showWalletScreen() {
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_screen_content, new WalletScreen());
        transaction.commit();
    }

    @Override
    public void showChatsScreen() {
        if (chatsScreen == null) { chatsScreen = new ChatsScreen(); }
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_screen_content, chatsScreen);
        transaction.commit();
    }

    @Override
    public void showSettingsScreen() {
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_screen_content, new SettingsScreen());
        transaction.commit();
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
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: {
                FragmentManager supportFragmentManager = getSupportFragmentManager();
                BottomNavigationDrawerFragment bottomNavDrawerFragment = new BottomNavigationDrawerFragment();
                bottomNavDrawerFragment.setMainPresenter(presenter);
                bottomNavDrawerFragment.show(supportFragmentManager, bottomNavDrawerFragment.getTag());

                return true;
            }
        }
        return false;
    }

    private Navigator navigator = new DefaultNavigator(this) {
        @Override
        protected void forward(Forward forwardCommand) {
            switch (forwardCommand.getScreenKey()){
                case Screens.MESSAGES_SCREEN: {
                    Intent intent = new Intent(getApplicationContext(), MessagesScreen.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(MessagesScreen.ARG_CHAT, (String)forwardCommand.getTransitionData());
                    intent.putExtras(bundle);

                    startActivity(intent);
                }
                break;

                case Screens.CREATE_CHAT_SCREEN: {

                    BottomCreateChatFragment createChatFragment = new BottomCreateChatFragment();
                    FragmentManager supportFragmentManager = getSupportFragmentManager();
                    createChatFragment.show(supportFragmentManager, createChatFragment.getTag());
                }
                break;

                case Screens.SCAN_QRCODE_SCREEN: {
                    Intent intent = new Intent(getApplicationContext(), ScanQrCodeScreen.class);
                    startActivityForResult(intent, Constants.SCAN_QR_CODE_RESULT);
                }
                break;

                case Screens.NODES_LIST_SCREEN: {
                    Intent intent = new Intent(getApplicationContext(), NodesListScreen.class);
                    startActivity(intent);
                }
                break;

                case Screens.PUSH_SUBSCRIPTION_SCREEN: {
                    Intent intent = new Intent(getApplicationContext(), PushSubscriptionScreen.class);
                    startActivity(intent);
                }
                break;

                case Screens.PINCODE_SCREEN: {
                    Bundle bundle = (Bundle) forwardCommand.getTransitionData();
                    Intent intent = new Intent(getApplicationContext(), PincodeScreen.class);
                    intent.putExtras(bundle);

                    startActivity(intent);
                }
                break;
            }
        }

        @Override
        protected void back(Back backCommand) {

        }

        @Override
        protected void backTo(BackTo backToCommand) {

        }

        @Override
        protected void message(SystemMessage systemMessageCommand) {
            Toast.makeText(getApplicationContext(), systemMessageCommand.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        protected void replace(Replace command) {

        }
    };
}
