package im.adamant.android.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class AlternativePingHelper {
    private final static int PRIMARY_DEFAULT_PING_PORT = 443;
    private final static int SECONDARY_DEFAULT_PING_PORT = 80;

    public static class Ping {
        public String net = "NO_CONNECTION";
        public String host;
        public String ip;
        public boolean reachable = false;
        public int dns = Integer.MAX_VALUE;
        public int cnt = Integer.MAX_VALUE;
    }

    public static Ping ping(Uri url, Context ctx) {
        Ping r = new Ping();
        if (isNetworkConnected(ctx)) {
            r.net = getNetworkType(ctx);
            try {
                String hostAddress;
                long start = System.currentTimeMillis();
                hostAddress = InetAddress.getByName(url.getHost()).getHostAddress();
                long dnsResolved = System.currentTimeMillis();

                connectToSocket(hostAddress, url);

                long probeFinish = System.currentTimeMillis();
                r.dns = (int) (dnsResolved - start);
                r.cnt = (int) (probeFinish - dnsResolved);
                r.host = url.getHost();
                r.ip = hostAddress;
                r.reachable = true;
            }
            catch (Exception ex) {
                LoggerHelper.e("PING","Unable to ping");
            }
        }

        return r;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Nullable
    public static String getNetworkType(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        if (activeNetwork != null) {
            return activeNetwork.getTypeName();
        }
        return null;
    }

    private static void connectToSocket(String hostAddress, Uri url) {
        int port = url.getPort();
        if (port == -1){
            try (Socket socket = new Socket(hostAddress, PRIMARY_DEFAULT_PING_PORT)){} catch (IOException ex){
                try (Socket socket = new Socket(hostAddress, SECONDARY_DEFAULT_PING_PORT)){} catch (IOException ex1){
                    //Nothing to do.
                }
            }
        } else {
            try (Socket socket = new Socket(hostAddress, port)){} catch (IOException ex){
                //Nothing to do.
            }
        }
    }
}
