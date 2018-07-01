package im.adamant.android.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.arellomobile.mvp.MvpAppCompatActivity;

import butterknife.ButterKnife;
import im.adamant.android.services.AdamantBalanceUpdateService;
import im.adamant.android.services.ServerNodesPingService;

public abstract class BaseActivity extends MvpAppCompatActivity {
    private boolean pingServiceBound = false;
    private boolean admServiceBound = false;
    protected AdamantBalanceUpdateService balanceUpdateService;

    public abstract int getLayoutId();

    public abstract boolean withBackButton();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutId());
        ButterKnife.bind(this);

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

        Intent serverNodePingIntent = new Intent(this, ServerNodesPingService.class);
        bindService(serverNodePingIntent, pingServiceConnection, Context.BIND_AUTO_CREATE);

        Intent admBalanceIntent = new Intent(this, AdamantBalanceUpdateService.class);
        bindService(admBalanceIntent, admBalanceServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (pingServiceBound) {
            unbindService(pingServiceConnection);
            pingServiceBound = false;
        }

        if (admServiceBound) {
            unbindService(admBalanceServiceConnection);
            admServiceBound = false;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


    private ServiceConnection pingServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            pingServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            pingServiceBound = false;
        }
    };

    private ServiceConnection admBalanceServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            admServiceBound = true;
            AdamantBalanceUpdateService.LocalBinder binder = (AdamantBalanceUpdateService.LocalBinder) service;
            balanceUpdateService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            admServiceBound = false;
            balanceUpdateService = null;
        }
    };
}
