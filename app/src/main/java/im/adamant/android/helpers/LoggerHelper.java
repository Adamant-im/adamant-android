package im.adamant.android.helpers;

import android.util.Log;

import im.adamant.android.BuildConfig;

public class LoggerHelper {
    public static void e(String tag, String text, Throwable t) {
        if (!isAvailableLogging()){return;}
        if (t == null){
            Log.e(String.valueOf(tag), String.valueOf(text));
        } else {
            Log.e(String.valueOf(tag), String.valueOf(text), t);
        }
    }

    public static void e(String tag, String text) {
        if (!isAvailableLogging()){return;}
        Log.e(String.valueOf(tag), String.valueOf(text));
    }

    public static void d(String tag, String text, Throwable t) {
        if (!isAvailableLogging()){return;}
        if (t == null){
            Log.d(String.valueOf(tag), String.valueOf(text));
        } else {
            Log.d(String.valueOf(tag), String.valueOf(text), t);
        }
    }

    public static void d(String tag, String text) {
        if (!isAvailableLogging()){return;}
        Log.d(String.valueOf(tag), String.valueOf(text));
    }

    public static void w(String tag, String text, Throwable t) {
        if (!isAvailableLogging()){return;}
        if (t == null){
            Log.w(String.valueOf(tag), String.valueOf(text));
        } else {
            Log.w(String.valueOf(tag), String.valueOf(text), t);
        }
    }

    public static void w(String tag, String text) {
        if (!isAvailableLogging()){return;}
        Log.w(String.valueOf(tag), String.valueOf(text));
    }

    private static boolean isAvailableLogging() {
        return BuildConfig.DEBUG || BuildConfig.RELEASE_LOG;
    }
}
