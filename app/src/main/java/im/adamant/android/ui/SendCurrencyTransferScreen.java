package im.adamant.android.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import im.adamant.android.R;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import dagger.android.AndroidInjection;
import im.adamant.android.Screens;
import im.adamant.android.ui.adapters.SendCurrencyFragmentAdapter;
import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.commands.Back;
import ru.terrakok.cicerone.commands.BackTo;
import ru.terrakok.cicerone.commands.Command;
import ru.terrakok.cicerone.commands.Forward;
import ru.terrakok.cicerone.commands.SystemMessage;

public class SendCurrencyTransferScreen extends BaseActivity implements HasSupportFragmentInjector {
    public static final String ARG_COMPANION_ID = "companion_id";

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    @Inject
    NavigatorHolder navigatorHolder;

//    @Inject
//    Provider<SendCurrencyPresenter> presenterProvider;
//
    @Inject
    SendCurrencyFragmentAdapter adapter;

    @BindView(R.id.activity_currency_transfer_tab_currencies) TabLayout tabs;
    @BindView(R.id.activity_currency_transfer_vp_swipe_slider) ViewPager slider;

    @Override
    public int getLayoutId() {
        return R.layout.activity_currency_transfer_screen;
    }

    @Override
    public boolean withBackButton() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(ARG_COMPANION_ID)) {
                String companionId = getIntent().getStringExtra(ARG_COMPANION_ID);
                adapter.setCompanionId(companionId);
            }
        }

        slider.setAdapter(adapter);
        tabs.setupWithViewPager(slider);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
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

    private Navigator navigator = new Navigator() {
        @Override
        public void applyCommands(Command[] commands) {
            for (Command command : commands){
                apply(command);
            }
        }

        private void apply(Command command){
            if (command instanceof BackTo) {
                BackTo backTo = (BackTo)command;
                switch (backTo.getScreenKey()) {
                    case Screens.MESSAGES_SCREEN: {
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
