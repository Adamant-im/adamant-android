package im.adamant.android.avatars;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Pair;

import io.reactivex.Single;

public class RoundWithBorderAvatar implements Avatar {
    private Avatar delegate;
    private int paddingSizePx;
    private int borderSizePx;

    public RoundWithBorderAvatar(Avatar delegate, int paddingSizePx, int borderSizePx) {
        this.delegate = delegate;
        this.paddingSizePx = paddingSizePx;
        this.borderSizePx = borderSizePx;
    }

    @Override
    public Single<Bitmap> build(String key, int sizePx) {

        return Single.fromCallable(() -> {
                    Bitmap.Config cfg = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = Bitmap.createBitmap(sizePx, sizePx, cfg);

                    Canvas canvas = new Canvas(bitmap);

                    int innerSizePx = getGraphics().drawCircleWithBorder(sizePx, borderSizePx, canvas);
                    innerSizePx = innerSizePx - (paddingSizePx * 2);

                    return new Pair<>(innerSizePx, bitmap);
                })
                .flatMap(pair -> {
                    int innerSizePx = pair.first;
                    Bitmap background = pair.second;

                    return delegate
                            .build(key, innerSizePx)
                            .map(avatar -> {
                                Canvas roundedCanvas = new Canvas(background);
                                roundedCanvas.drawBitmap(
                                        avatar,
                                        borderSizePx + paddingSizePx,
                                        borderSizePx + paddingSizePx,
                                        new Paint()
                                );

                                return background;
                            });
                 });

    }

    @Override
    public AvatarGraphics getGraphics() {
        return delegate.getGraphics();
    }
}
