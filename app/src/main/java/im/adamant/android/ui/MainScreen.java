package im.adamant.android.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.Serializable;
import java.lang.ref.WeakReference;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import butterknife.BindView;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.presenters.MainPresenter;
import im.adamant.android.ui.adapters.FragmentsAdapter;
import im.adamant.android.ui.fragments.BaseFragment;
import im.adamant.android.ui.mvp_view.MainView;
import im.adamant.android.ui.mvp_view.WalletView;
import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.commands.Command;
import ru.terrakok.cicerone.commands.Forward;
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

    @Named("main")
    @Inject
    FragmentsAdapter mainAdapterReference;

    @BindView(R.id.main_screen_content) ViewPager content;
    @BindView(R.id.main_screen_navigation) BottomNavigationView navigation;

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

        navigation.setOnNavigationItemSelectedListener(item -> {
            BaseFragment fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_wallet:
                    presenter.onSelectedWalletTab();
                    return true;
                case R.id.navigation_chats:
                    presenter.onSelectedChatsTab();
                    return true;
                case R.id.navigation_settings:
                    presenter.onSelectedSettingsTab();
                    return true;
            }
            return false;
        });

        content.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTitle(mainAdapterReference.getPageTitle(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        content.setAdapter(mainAdapterReference);
        setTitle(mainAdapterReference.getPageTitle(0));

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            String deviceToken = instanceIdResult.getToken();
            presenter.onGetNotificationToken(deviceToken);
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        navigatorHolder.setNavigator(navigator);

        if (mainAdapterReference != null){
            setTitle(mainAdapterReference.getPageTitle(content.getCurrentItem()));
        }
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
                        bundle.putString(MessagesScreen.ARG_CHAT, (String)forward.getTransitionData());
                        intent.putExtras(bundle);

                        startActivity(intent);
                    }
                    break;

                    case Screens.CREATE_CHAT_SCREEN: {
                        Intent intent = new Intent(getApplicationContext(), CreateChatScreen.class);
                        startActivity(intent);
                    }
                    break;

                    case Screens.LOGIN_SCREEN: {
                        Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
                        startActivity(intent);
                        MainScreen.this.finish();
                    }
                    break;

                    case Screens.WALLET_SCREEN: {
                        content.setCurrentItem(0);
                    }
                    break;

                    case Screens.CHATS_SCREEN: {
                        content.setCurrentItem(1);
                    }
                    break;

                    case Screens.SETTINGS_SCREEN: {
                        content.setCurrentItem(2);
                    }
                    break;

                    case WalletView.SHOW_FREE_TOKEN_PAGE : {
                        String url = getString(R.string.free_token_url) + forward.getTransitionData();
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                    }
                    break;

                    case Screens.SPLASH_SCREEN: {
                        Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
                        startActivity(intent);
                        finish();
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
