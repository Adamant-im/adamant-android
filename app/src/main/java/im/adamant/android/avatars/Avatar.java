package im.adamant.android.avatars;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.util.TypedValue;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public interface Avatar {
    Single<Bitmap> build(String key, int sizePx);
    AvatarGraphics getGraphics();
}
