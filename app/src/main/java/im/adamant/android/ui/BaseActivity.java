package im.adamant.android.ui;

import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.franmontiel.localechanger.LocaleChanger;

import butterknife.ButterKnife;
import im.adamant.android.R;
import im.adamant.android.services.AdamantBalanceUpdateService;
import im.adamant.android.services.ServerNodesPingService;

import static android.content.pm.PackageManager.GET_META_DATA;

public abstract class BaseActivity extends MvpAppCompatActivity {
    protected AdamantBalanceUpdateService balanceUpdateService;
    private ServiceConnection pingServiceConnection;
    private ServiceConnection admBalanceServiceConnection;

    private TextView titleView;

    public abstract int getLayoutId();

    public abstract boolean withBackButton();

    public void onClickTitle(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutId());
        ButterKnife.bind(this);

        createCustomTitle();

        if (withBackButton()){
            android.support.v7.app.ActionBar supportActionBar = getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.setHomeButtonEnabled(true);
                supportActionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        initConnections();

        Intent serverNodePingIntent = new Intent(this, ServerNodesPingService.class);
        bindService(serverNodePingIntent, pingServiceConnection, Context.BIND_AUTO_CREATE);

        Intent admBalanceIntent = new Intent(this, AdamantBalanceUpdateService.class);
        bindService(admBalanceIntent, admBalanceServiceConnection, Context.BIND_AUTO_CREATE);
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void setTitle(CharSequence title) {
        if (titleView != null){
            titleView.setText(title);
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
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Disable the default and enable the custom
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            View customView = getLayoutInflater().inflate(R.layout.custom_action_bar, null);

            titleView = customView.findViewById(R.id.actionbarTitle);

            // Set the on click listener for the title
            titleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickTitle();
                }
            });
            // Apply the custom view
            actionBar.setCustomView(customView);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        newBase = LocaleChanger.configureBaseContext(newBase);
        super.attachBaseContext(newBase);
    }
}
