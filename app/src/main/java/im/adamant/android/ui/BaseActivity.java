package im.adamant.android.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;

import com.arellomobile.mvp.MvpDelegate;
import com.franmontiel.localechanger.LocaleChanger;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import im.adamant.android.R;
import im.adamant.android.services.AdamantBalanceUpdateService;
import im.adamant.android.services.ServerNodesPingService;

import static android.content.pm.PackageManager.GET_META_DATA;
/**
 * This class has been redesigned to work with the Moxy framework.
 * This is necessary because if you use the MvpAppCompatActivity class, then the syntax highlighting breaks.
 * Because there is a code from the support library.
 * */
public abstract class BaseActivity extends AppCompatActivity {
    private MvpDelegate<? extends BaseActivity> mMvpDelegate;

    protected AdamantBalanceUpdateService balanceUpdateService;
    private ServiceConnection pingServiceConnection;
    private ServiceConnection admBalanceServiceConnection;

    private TextView titleView;
    private TextView subTitleView;
    private View customTitleView;

    public abstract int getLayoutId();

    public abstract boolean withBackButton();

    public void onClickTitle(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getMvpDelegate().onCreate(savedInstanceState);

        //TODO: deny automatic screenshot but allow screenshot triggered by user
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
//                WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(getLayoutId());
        ButterKnife.bind(this);

        createCustomTitle();

        if (withBackButton()){
            androidx.appcompat.app.ActionBar supportActionBar = getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.setHomeButtonEnabled(true);
                supportActionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        getMvpDelegate().onAttach();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getMvpDelegate().onAttach();

        initConnections();

        Intent serverNodePingIntent = new Intent(this, ServerNodesPingService.class);
        bindService(serverNodePingIntent, pingServiceConnection, Context.BIND_AUTO_CREATE);

        Intent admBalanceIntent = new Intent(this, AdamantBalanceUpdateService.class);
        bindService(admBalanceIntent, admBalanceServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getMvpDelegate().onSaveInstanceState(outState);
        getMvpDelegate().onDetach();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (pingServiceConnection != null) {
            unbindService(pingServiceConnection);
            pingServiceConnection = null;
        }

        if (admBalanceServiceConnection != null) {
            unbindService(admBalanceServiceConnection);
            balanceUpdateService = null;
            admBalanceServiceConnection = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        getMvpDelegate().onDetach();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        getMvpDelegate().onDestroyView();

        if (isFinishing()) {
            getMvpDelegate().onDestroy();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void setTitle(CharSequence title) {
        if (titleView != null){
            titleView.setText(title);
            rebuildTitleView();
        }
    }

    public void setSubTitle(CharSequence subTitle) {
        if (subTitleView != null){
            subTitleView.setText(subTitle);
            rebuildTitleView();
        }
    }

    private void rebuildTitleView() {
        if (subTitleView == null || titleView == null){return;}

        String title = titleView.getText().toString();
        String subTitle = subTitleView.getText().toString();
        boolean hideSubview = (subTitle.isEmpty()) || (title.equalsIgnoreCase(subTitle));

        if (hideSubview){
            subTitleView.setVisibility(View.GONE);
        } else {
            subTitleView.setVisibility(View.VISIBLE);
        }

    }

    private void initConnections() {
        if (pingServiceConnection == null){
            pingServiceConnection = new ServiceConnection() {

                @Override
                public void onServiceConnected(ComponentName className, IBinder service) {

                }

                @Override
                public void onServiceDisconnected(ComponentName arg0) {

                }
            };
        }

        if (admBalanceServiceConnection == null){
            admBalanceServiceConnection = new ServiceConnection() {

                @Override
                public void onServiceConnected(ComponentName className, IBinder service) {
                    AdamantBalanceUpdateService.LocalBinder binder = (AdamantBalanceUpdateService.LocalBinder) service;
                    balanceUpdateService = binder.getService();
                }

                @Override
                public void onServiceDisconnected(ComponentName arg0) {
                    balanceUpdateService = null;
                }
            };
        }
    }

    private void resetTitle() {
        try {
            int label = getPackageManager().getActivityInfo(getComponentName(), GET_META_DATA).labelRes;
            if (label != 0) {
                setTitle(label);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void createCustomTitle() {
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Disable the default and enable the custom
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            customTitleView = getLayoutInflater().inflate(R.layout.custom_action_bar, null);

            titleView = customTitleView.findViewById(R.id.actionbarTitle);
            subTitleView = customTitleView.findViewById(R.id.actionbarSubTitle);

            // Set the on click listener for the title
            customTitleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickTitle();
                }
            });
            // Apply the custom view
            actionBar.setCustomView(customTitleView);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        newBase = LocaleChanger.configureBaseContext(newBase);
        super.attachBaseContext(newBase);
    }

    /**
     * @return The {@link MvpDelegate} being used by this Activity.
     */
    public MvpDelegate getMvpDelegate() {
        if (mMvpDelegate == null) {
            mMvpDelegate = new MvpDelegate<>(this);
        }
        return mMvpDelegate;
    }
}
