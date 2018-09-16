package im.adamant.android.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.stealthcopter.networktools.Ping;
import com.stealthcopter.networktools.ping.PingResult;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.android.AndroidInjection;
import im.adamant.android.BuildConfig;
import im.adamant.android.core.entities.ServerNode;
import im.adamant.android.dagger.ServerNodePingServiceModule;
import im.adamant.android.helpers.AlternativePingHelper;
import im.adamant.android.helpers.Settings;
import im.adamant.android.rx.ObservableRxList;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static im.adamant.android.core.entities.ServerNode.UNAVAILABLE_PING;

public class ServerNodesPingService extends Service {
    private static final String TAG = "PingService";
    public static final int PING_TIMEOUT = 15000;

    private final IBinder binder = new LocalBinder();

    @Named(ServerNodePingServiceModule.NAME)
    @Inject
    CompositeDisposable compositeDisposable;

    @Inject
    Settings settings;

    @Inject
    Context context;

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();

        pingServers(settings.getNodes());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void pingServers(ObservableRxList<ServerNode> nodes) {
        Context ctx = getApplicationContext();
        Disposable disposable = nodes
                .getCurrentList()
                .observeOn(Schedulers.io())
                .doOnNext(serverNode -> {
                    try {
                        Uri uri = Uri.parse(serverNode.getUrl());
                        InetAddress address = InetAddress.getByName(uri.getHost());

                        PingResult pingResult = ping(address, uri, ctx);

                        if ((!"localhost".equalsIgnoreCase(address.getHostName()))){
                            parsePingValue(serverNode, pingResult);
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
                .doOnNext(nodes::wasUpdated)
                .repeatWhen((completed) -> completed.delay(BuildConfig.PING_SECONDS_DELAY, TimeUnit.SECONDS))
                .subscribe();

        compositeDisposable.add(disposable);
    }

    private static void parsePingValue(ServerNode serverNode, PingResult pingResult) {
        if (pingResult.isReachable()) {
            if (serverNode.getStatus() != ServerNode.Status.CONNECTED){
                serverNode.setStatus(ServerNode.Status.ACTIVE);
            }
            serverNode.setPingInMilliseconds(pingResult.getTimeTaken());
        } else {
            serverNode.setStatus(ServerNode.Status.UNAVAILABLE);
            serverNode.setPingInMilliseconds(UNAVAILABLE_PING);
        }
    }

    private static PingResult ping(InetAddress address, Uri uri, Context context) throws UnknownHostException {
        PingResult pingResult = Ping.onAddress(address).setTimeOutMillis(PING_TIMEOUT).doPing();
        if ((pingResult.error != null) && (!pingResult.error.isEmpty())){
            AlternativePingHelper.Ping ping = AlternativePingHelper.ping(uri, context);
            PingResult result = new PingResult(address);
            result.timeTaken = ping.cnt;
            result.isReachable = ping.reachable;

            pingResult = result;
        }

        return pingResult;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null){
            compositeDisposable.dispose();
        }
    }

    public class LocalBinder extends Binder {}
}
