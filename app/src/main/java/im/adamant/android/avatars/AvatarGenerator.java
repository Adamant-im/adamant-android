package im.adamant.android.avatars;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.Random;

import im.adamant.android.core.encryption.Hex;

import static im.adamant.android.avatars.Triangle.Direction.LEFT;
import static im.adamant.android.avatars.Triangle.Direction.RIGHT;
import static java.lang.Math.ceil;
import static java.lang.Math.sqrt;

public class AvatarGenerator {

    private AvatarCache avatarCache;
    private static final int[][] colors = new int[][]{
        {
            Color.parseColor("#ffffffff"), //background
            Color.parseColor("#ff8786ff"), // main
            Color.parseColor("#7788faff"), // 2dary
            Color.parseColor("#ffcf9bff") // 2dary
        },
        {
            Color.parseColor("#ffffffff"), //background
            Color.parseColor("#7788faff"), // main
            Color.parseColor("#51b5f1ff"), // 2dary
            Color.parseColor("#a8d9f7ff") // 2dary
        },
        {
            Color.parseColor("#ffffffff"), //background
            Color.parseColor("#7ce3d8ff"), // main
            Color.parseColor("#ffdbb4ff"), // 2dary
            Color.parseColor("#c3c4cbff") // 2dary
        }
    };


    public AvatarGenerator(AvatarCache avatarCache) {
        this.avatarCache = avatarCache;
    }

    public Bitmap buildAvatar(String key, int size) {
        Bitmap bitmap = avatarCache.get(key);

        if (bitmap == null){
            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            bitmap = Bitmap.createBitmap(size, size, conf);
            Canvas canvas = new Canvas(bitmap);
            hexa16(key, colors, size, canvas);
        }
        return bitmap;
    }

    private void hexa16(String key, int[][] colors, int size, Canvas canvas) {
        int fringeSize = size / 6;
        float distance = distanceTo3rdPoint(fringeSize);
        int lines = size / fringeSize;
        float offset = ((fringeSize - distance) * lines) / 2;

        int[] fillTriangle = triangleColors(0, key, colors, lines);
        int transparent = Color.TRANSPARENT;

        PositionChecker isLeft = (v) -> (v % 2) == 0;
        PositionChecker isRight = (v) -> (v % 2) != 0;


        int L = lines;
        int hL = L / 2;

        for (int xL = 0; xL < hL; xL++){
            for (int yL = 0; yL < L; yL++){
                if (isOutsideHexagon(xL, yL, lines)) {
                    continue;
                }

                float x1, x2, y1, y2, y3;

                if ((xL % 2) == 0) {
                    float[] result = right1stTriangle(xL, yL, fringeSize, distance);
                    x1 = result[0];
                    y1 = result[1];
                    x2 = result[2];
                    y2 = result[3];
                    y3 = result[5];
                } else {
                    float[] result = left1stTriangle(xL, yL, fringeSize, distance);
                    x1 = result[0];
                    y1 = result[1];
                    x2 = result[2];
                    y2 = result[3];
                    y3 = result[5];
                }

                float[] xs = new float[]{x2 + offset, x1 + offset, x2 + offset};
                float[] ys = new float[]{y1, y2, y3};

                int fill = canFill(xL, yL, fillTriangle, isLeft, isRight);
                if (fill != 0) {
                    drawPoligon(xs, ys, fill, canvas);
                } else {
                    drawPoligon(xs, ys, transparent, canvas);
                }

                float[] xsMirror = mirrorCoordinates(xs, lines, distance, offset * 2);
                int xLMirror = lines - xL - 1;
                int yLMirror = yL;

                int fill2 = canFill(xLMirror, yLMirror, fillTriangle, isLeft, isRight);

                if (fill2 != 0) {
                    drawPoligon(xsMirror, ys, fill2, canvas);
                } else {
                    drawPoligon(xsMirror, ys, transparent, canvas);
                }

                float x11, x12, y11, y12, y13;

                if ((xL % 2) == 0) {
                    float[] result = left2ndTriangle(xL, yL, fringeSize, distance);
                    x11 = result[0];
                    y11 = result[1];
                    x12 = result[2];
                    y12 = result[3];
                    y13 = result[5];

                    // in order to have a perfect hexagon,
                    // we make sure that the previous triangle and this one touch each other in this point.
                    y12 = y3;
                } else {
                    float[] result = right2ndTriangle(xL, yL, fringeSize, distance);
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
                int fill3 = canFill(xL, yL, fillTriangle, isRight, isLeft);
                if (fill3 != 0) {
                    drawPoligon(xs1, ys1, fill3, canvas);
                } else {
                    drawPoligon(xs1, ys1, transparent, canvas);
                }

                xs1 = mirrorCoordinates(xs1, lines, distance, offset * 2);

                int fill4 = canFill(xLMirror, yLMirror, fillTriangle, isRight, isLeft);

                if (fill4 != 0) {
                    drawPoligon(xs1, ys1, fill4, canvas);
                } else {
                    drawPoligon(xs1, ys1, transparent, canvas);
                }

            }
        }
    }

    private void drawPoligon(float[] xs, float[] ys, int color, Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(5);

        Path path = new Path();

        for (int i = 0; i < xs.length; i++){
            float x = xs[i];
            float y = ys[i];
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }
        //TODO: CubicTo

        path.close();
        canvas.drawPath(path, paint);
    }

    private float distanceTo3rdPoint(float ac) {
        // distance from center of vector to third point of equilateral triangles
        // ABC triangle, O is the center of AB vector
        // OC = SQRT(AC^2 - AO^2)
        return (float) ceil(sqrt((ac * ac) - (ac/2 * ac/2)));
    }

    // right1stTriangle computes a right oriented triangle '>'
    private float[] right1stTriangle(float xL, float yL, float fringeSize, float distance) {
        float x1 = xL * distance;
        float x2 = xL * distance + distance;
        float x3 = x1;
        float y1 = yL * fringeSize;
        float y2 = y1 + fringeSize / 2;
        float y3 = yL * fringeSize + fringeSize;
        return new float[]{x1, y1, x2, y2, x3, y3};
    }

    // left1stTriangle computes the coordinates of a left oriented triangle '<'
    private float[] left1stTriangle(float xL, float yL, float fringeSize, float distance) {
        float x1 = xL * distance + distance;
        float x2 = xL * distance;
        float x3 = x1;
        float y1 = yL * fringeSize;
        float y2 = y1 + fringeSize / 2;
        float y3 = yL * fringeSize + fringeSize;
        return new float[]{x1, y1, x2, y2, x3, y3};
    }

    // left2ndTriangle computes the coordinates of a left oriented triangle '<'
    private float[]  left2ndTriangle(float xL, float yL, float fringeSize, float distance) {
        float x1 = xL * distance + distance;
        float x2 = xL * distance;
        float x3 = x1;
        float y1 = yL * fringeSize + fringeSize / 2;
        float y2 = y1 + fringeSize / 2;
        float y3 = yL * fringeSize + fringeSize + fringeSize / 2;
        return new float[]{x1, y1, x2, y2, x3, y3};
    }

    // right2ndTriangle computes the coordinates of a right oriented triangle '>'
    private float[]  right2ndTriangle(float xL, float yL, float fringeSize, float distance) {
        float x1 = xL * distance;
        float x2 = xL * distance + distance;
        float x3 = x1;
        float y1 = yL * fringeSize + fringeSize / 2;
        float y2 = yL + fringeSize;
        float y3 = yL * fringeSize + fringeSize / 2 + fringeSize;
        return new float[]{x1, y1, x2, y2, x3, y3};
    }

    private float[] mirrorCoordinates(float[] xs, float lines, float fringeSize, float offset) {
        //TODO: null checks
        float[] xsMirror = new float[xs.length];

        for (int i = 0; i < xs.length; i++) {
            xsMirror[i] = (lines * fringeSize) - xs[i] + offset;
        }

        return xsMirror;
    }

    //TODO: Make private inner methods
    private int[] triangleColors(int id, String key, int[][] colors, int lines) {
        //TODO: check null
        int[] tColors = new int[Triangle.triangles[id].length];

        long seed = 0;

        String keyHash = Hex.md5Hash(key);

        for(char s : keyHash.toCharArray()){
            seed += s;
        }

        Random rnd = new Random(seed);
        int minRand = 0;
        int maxRand = Integer.MAX_VALUE / 2;

        int[] rndColors = colors[Hex.randRange(rnd, minRand, maxRand) % colors.length];

        for (int i = 0; i < Triangle.triangles[id].length; i++) {
            Triangle t = Triangle.triangles[id][i];
            int x = t.getX();
            int y = t.getY();
            int index = (x + 3 * y + lines + rnd.nextInt()) % 15;
            int color = PickColor(key, rndColors, index);
            tColors[i] = color;
        }

        return tColors;
    }

    private boolean isOutsideHexagon(int xL, int yL, int lines) {
        return !isFill1InHexagon(xL, yL, lines) && !isFill2InHexagon(xL, yL, lines);
    }

    private boolean isFill1InHexagon(int xL, int yL, int lines) {
        int half = lines / 2;
        int start = half / 2;

        if (xL < (start + 1)) {
            if ((yL > start - 1) && (yL < start + half + 1)) {
                return true;
            }
        }

        if (xL == half - 1) {
            if ((yL > start - 1 - 1) && (yL < start + half + 1 + 1)) {
                return true;
            }
        }
        return false;
    }

    private boolean isFill2InHexagon(int xL, int yL, int lines) {
        int half = lines / 2;
        int start = half / 2;

        if (xL < start) {
            if ((yL > start - 1) && (yL < start + half)) {
                return true;
            }
        }

        if (xL == 1) {
            if ((yL > start - 1 - 1) && (yL < start + half + 1)) {
                return true;
            }
        }

        if (xL == half - 1) {
            return (yL > start - 1 - 1) && (yL < start + half + 1);
        }
        return false;
    }

    // PickColor returns a color given a key string, an array of colors and an index.
    // key: should be a md5 hash string.
    // index: is an index from the key string. Should be in interval [0, 16]
    // Algorithm: PickColor converts the key[index] value to a decimal value.
    // We pick the ith colors that respects the equality value%numberOfColors == i.
    private int PickColor(String key, int[] colors, int index) {
        int n = colors.length;
        int i = PickIndex(key, n, index);
        return colors[i];
    }

    // PickIndex returns an index of given a key string, the size of an array of colors
    //  and an index.
    // key: should be a md5 hash string.
    // index: is an index from the key string. Should be in interval [0, 16]
    // Algorithm: PickIndex converts the key[index] value to a decimal value.
    // We pick the ith index that respects the equality value%sizeOfArray == i.
    private int PickIndex(String key, int n, int index) {
        String keyHash = Hex.md5Hash(key);
        char s = keyHash.charAt(index);
//        let s = String(key.md5()[index])
        int r = ((byte) s) & 0xff;
//        let r = Int([UInt8](s.utf8).first ?? 0)

        for(int i = 0; i < n; i++){
            if (r % n == i){
                return i;
            }
        }

        return 0;
    }

    // canFill returns a fill svg string given position. the fill is computed to be a rotation of the
    // triangle 0 with the 'fills' array given as param.
    private int canFill(int x, int y, int[] fills, PositionChecker isLeft, PositionChecker isRight) {
        Triangle l = new Triangle(x, y, LEFT);
        Triangle r = new Triangle(x, y, RIGHT);

        if (isLeft.isIt(x) && l.isInTriangle()) {
            int rid = l.rotationID();
            return fills[rid];
        } else if (isRight.isIt(x) && r.isInTriangle()) {
            int rid = r.rotationID();
            return fills[rid];
        }

        return 0;
    }
}
