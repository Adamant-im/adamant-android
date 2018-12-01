package im.adamant.android.avatars;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Pair;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SquareAvatar implements Avatar {
    private AvatarGraphics graphics;

    public SquareAvatar(AvatarGraphics graphics) {
        this.graphics = graphics;
    }

    @Override
    public Single<Bitmap> build(String key, int sizePx) {
        return Single.fromCallable(() -> {
                    Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                    Bitmap avatarBitmap = Bitmap.createBitmap(sizePx, sizePx, conf);
                    avatarBitmap.setHasAlpha(true);

                    Canvas canvas = new Canvas(avatarBitmap);

                    hexa16(key, sizePx, canvas);

                    return avatarBitmap;
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public AvatarGraphics getGraphics() {
        return graphics;
    }

    //TODO: Refactor this
    private void hexa16(String key, int size, Canvas canvas) {
        int fringeSize = size / 6;
        float distance = graphics.distanceTo3rdPoint(fringeSize);
        int lines = size / fringeSize;
        float offset = ((fringeSize - distance) * lines) / 2;

        int[] fillTriangle = graphics.triangleColors(0, key, lines);
        int transparent = Color.TRANSPARENT;

        PositionChecker isLeft = (v) -> (v % 2) == 0;
        PositionChecker isRight = (v) -> (v % 2) != 0;


        int L = lines;
        int hL = L / 2;

        for (int xL = 0; xL < hL; xL++){
            for (int yL = 0; yL < L; yL++){
                if (graphics.isOutsideHexagon(xL, yL, lines)) {
                    continue;
                }

                float x1, x2, y1, y2, y3;

                if ((xL % 2) == 0) {
                    float[] result = graphics.right1stTriangle(xL, yL, fringeSize, distance);
                    x1 = result[0];
                    y1 = result[1];
                    x2 = result[2];
                    y2 = result[3];
                    y3 = result[5];
                } else {
                    float[] result = graphics.left1stTriangle(xL, yL, fringeSize, distance);
                    x1 = result[0];
                    y1 = result[1];
                    x2 = result[2];
                    y2 = result[3];
                    y3 = result[5];
                }

                float[] xs = new float[]{x2 + offset, x1 + offset, x2 + offset};
                float[] ys = new float[]{y1, y2, y3};

                int fill = graphics.canFill(xL, yL, fillTriangle, isLeft, isRight);
                if (fill != 0) {
                    graphics.drawPolygon(xs, ys, fill, canvas);
                } else {
                    graphics.drawPolygon(xs, ys, transparent, canvas);
                }

                float[] xsMirror = graphics.mirrorCoordinates(xs, lines, distance, offset * 2);
                int xLMirror = lines - xL - 1;
                int yLMirror = yL;

                int fill2 = graphics.canFill(xLMirror, yLMirror, fillTriangle, isLeft, isRight);

                if (fill2 != 0) {
                    graphics.drawPolygon(xsMirror, ys, fill2, canvas);
                } else {
                    graphics.drawPolygon(xsMirror, ys, transparent, canvas);
                }

                float x11, x12, y11, y12, y13;

                if ((xL % 2) == 0) {
                    float[] result = graphics.left2ndTriangle(xL, yL, fringeSize, distance);
                    x11 = result[0];
                    y11 = result[1];
                    x12 = result[2];
                    y12 = result[3];
                    y13 = result[5];

                    // in order to have a perfect hexagon,
                    // we make sure that the previous triangle and this one touch each other in this point.
                    y12 = y3;
                } else {
                    float[] result = graphics.right2ndTriangle(xL, yL, fringeSize, distance);
                    x11 = result[0];
                    y11 = result[1];
                    x12 = result[2];
                    y12 = result[3];
                    y13 = result[5];

                    // in order to have a perfect hexagon,
                    // we make sure that the previous triangle and this one touch each other in this point.
                    y12 = y1 + fringeSize;
                }

                float[] xs1 = new float[]{x12 + offset, x11 + offset, x12 + offset};
                float[] ys1 = new float[]{y11, y12, y13};

                // triangles that go to the right
                int fill3 = graphics.canFill(xL, yL, fillTriangle, isRight, isLeft);
                if (fill3 != 0) {
                    graphics.drawPolygon(xs1, ys1, fill3, canvas);
                } else {
                    graphics.drawPolygon(xs1, ys1, transparent, canvas);
                }

                xs1 = graphics.mirrorCoordinates(xs1, lines, distance, offset * 2);

                int fill4 = graphics.canFill(xLMirror, yLMirror, fillTriangle, isRight, isLeft);

                if (fill4 != 0) {
                    graphics.drawPolygon(xs1, ys1, fill4, canvas);
                } else {
                    graphics.drawPolygon(xs1, ys1, transparent, canvas);
                }

            }
        }
    }
}
