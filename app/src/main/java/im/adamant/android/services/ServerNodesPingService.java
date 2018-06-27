package im.adamant.android.services;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.stealthcopter.networktools.Ping;
import com.stealthcopter.networktools.ping.PingResult;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.android.AndroidInjection;
import im.adamant.android.BuildConfig;
import im.adamant.android.core.entities.ServerNode;
import im.adamant.android.dagger.ServerNodePingServiceModule;
import im.adamant.android.helpers.Settings;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static im.adamant.android.core.entities.ServerNode.UNAVAILABLE_PING;

public class ServerNodesPingService extends Service {
    private static final String TAG = "PingService";
    private final IBinder binder = new LocalBinder();

    @Named(ServerNodePingServiceModule.NAME)
    @Inject
    CompositeDisposable compositeDisposable;

    @Inject
    Settings settings;

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
        pingServers();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void pingServers() {
        Disposable disposable = settings
                .getNodes()
                .getCurrentList()
                .observeOn(Schedulers.io())
                .doOnNext(serverNode -> {
                    try {
                        Uri uri = Uri.parse(serverNode.getUrl());
                        InetAddress address = InetAddress.getByName(uri.getHost());

                        PingResult pingResult = Ping.onAddress(address).setTimeOutMillis(5000).doPing();
                        if ((!"localhost".equalsIgnoreCase(address.getHostName())) && (pingResult.isReachable())) {
                            if (serverNode.getStatus() != ServerNode.Status.CONNECTED){
                                serverNode.setStatus(ServerNode.Status.ACTIVE);
                            }
                            serverNode.setPingInMilliseconds(pingResult.getTimeTaken());
                        } else {
                            serverNode.setStatus(ServerNode.Status.UNAVAILABLE);
                            serverNode.setPingInMilliseconds(UNAVAILABLE_PING);
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();

                        serverNode.setStatus(ServerNode.Status.UNAVAILABLE);
                        serverNode.setPingInMilliseconds(UNAVAILABLE_PING);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(serverNode -> settings.getNodes().wasUpdated(serverNode))
                .repeatWhen((completed) -> completed.delay(BuildConfig.PING_SECONDS_DELAY, TimeUnit.SECONDS))
                .subscribe();

        compositeDisposable.add(disposable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null){
            compositeDisposable.dispose();
        }
    }

    public class LocalBinder extends Binder {
        ServerNodesPingService getService() {
            return ServerNodesPingService.this;
        }
    }
}
