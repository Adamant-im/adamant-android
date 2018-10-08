package im.adamant.android.avatars;

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

public class AvatarGraphics {
    private AvatarThemesProvider avatarThemesProvider;

    public AvatarGraphics(AvatarThemesProvider avatarThemesProvider) {
        this.avatarThemesProvider = avatarThemesProvider;
    }

    public int drawCircleWithBorder(int sizePx, int borderSizePx, Canvas canvas) {
        return drawCircleWithBorder(sizePx, borderSizePx, canvas, Color.WHITE);
    }

    public int drawCircleWithBorder(int sizePx, int borderSizePx, Canvas canvas, int color) {
        Paint transparent = new Paint();
        transparent.setAlpha(Color.TRANSPARENT);
        transparent.setColor(Color.TRANSPARENT);
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawRect(0,0, sizePx, sizePx, transparent);


        float cx = sizePx / 2;
        float r = cx - (borderSizePx * 2);

        Paint background = new Paint();
        background.setColor(color);
        background.setStyle(Paint.Style.FILL);

        canvas.drawCircle(cx, cx , r, background);

        Paint border = new Paint();
        border.setColor(avatarThemesProvider.provideBorderColor());
        border.setStrokeWidth(borderSizePx);
        border.setStyle(Paint.Style.STROKE);
        border.setAntiAlias(true);

        canvas.drawCircle(cx , cx, r, border);

        return sizePx - (borderSizePx * 2);
    }

    public void drawPolygon(float[] xs, float[] ys, int color, Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);

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

    public float distanceTo3rdPoint(float ac) {
        // distance from center of vector to third point of equilateral triangles
        // ABC triangle, O is the center of AB vector
        // OC = SQRT(AC^2 - AO^2)
        return (float) ceil(sqrt((ac * ac) - (ac/2 * ac/2)));
    }

    // right1stTriangle computes a right oriented triangle '>'
    public float[] right1stTriangle(float xL, float yL, float fringeSize, float distance) {
        float x1 = xL * distance;
        float x2 = xL * distance + distance;
        float x3 = x1;
        float y1 = yL * fringeSize;
        float y2 = y1 + fringeSize / 2;
        float y3 = yL * fringeSize + fringeSize;
        return new float[]{x1, y1, x2, y2, x3, y3};
    }

    // left1stTriangle computes the coordinates of a left oriented triangle '<'
    public float[] left1stTriangle(float xL, float yL, float fringeSize, float distance) {
        float x1 = xL * distance + distance;
        float x2 = xL * distance;
        float x3 = x1;
        float y1 = yL * fringeSize;
        float y2 = y1 + fringeSize / 2;
        float y3 = yL * fringeSize + fringeSize;
        return new float[]{x1, y1, x2, y2, x3, y3};
    }

    // left2ndTriangle computes the coordinates of a left oriented triangle '<'
    public float[]  left2ndTriangle(float xL, float yL, float fringeSize, float distance) {
        float x1 = xL * distance + distance;
        float x2 = xL * distance;
        float x3 = x1;
        float y1 = yL * fringeSize + fringeSize / 2;
        float y2 = y1 + fringeSize / 2;
        float y3 = yL * fringeSize + fringeSize + fringeSize / 2;
        return new float[]{x1, y1, x2, y2, x3, y3};
    }

    // right2ndTriangle computes the coordinates of a right oriented triangle '>'
    public float[]  right2ndTriangle(float xL, float yL, float fringeSize, float distance) {
        float x1 = xL * distance;
        float x2 = xL * distance + distance;
        float x3 = x1;
        float y1 = yL * fringeSize + fringeSize / 2;
        float y2 = yL + fringeSize;
        float y3 = yL * fringeSize + fringeSize / 2 + fringeSize;
        return new float[]{x1, y1, x2, y2, x3, y3};
    }

    public float[] mirrorCoordinates(float[] xs, float lines, float fringeSize, float offset) {
        //TODO: null checks
        float[] xsMirror = new float[xs.length];

        for (int i = 0; i < xs.length; i++) {
            xsMirror[i] = (lines * fringeSize) - xs[i] + offset;
        }

        return xsMirror;
    }

    //TODO: Make private inner methods
    public int[] triangleColors(int id, String key, int lines) {
        //TODO: check null
        int[] tColors = new int[Triangle.triangles[id].length];

        long seed = getPublicKeySeed(key);

        Random rnd = new Random(seed);
        int minRand = 0;
        int maxRand = Integer.MAX_VALUE / 2;

        int[] rndColors = avatarThemesProvider.provide(
                randRange(rnd, minRand, maxRand) % avatarThemesProvider.count()
        );

        for (int i = 0; i < Triangle.triangles[id].length; i++) {
            Triangle t = Triangle.triangles[id][i];
            int x = t.getX();
            int y = t.getY();
            int index = (x + 3 * y + lines + randRange(rnd, minRand, maxRand)) % 15;
            int color = PickColor(key, rndColors, index);
            tColors[i] = color;
        }

        return tColors;
    }

    public boolean isOutsideHexagon(int xL, int yL, int lines) {
        return !isFill1InHexagon(xL, yL, lines) && !isFill2InHexagon(xL, yL, lines);
    }

    public boolean isFill1InHexagon(int xL, int yL, int lines) {
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

    public boolean isFill2InHexagon(int xL, int yL, int lines) {
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
    public int PickColor(String key, int[] colors, int index) {
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
    public int PickIndex(String key, int n, int index) {
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
    public int canFill(int x, int y, int[] fills, PositionChecker isLeft, PositionChecker isRight) {
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

    public static int randRange(Random rnd, int min, int max) {
        return rnd.nextInt((max - min) + 1) + min;
    }

    public long getPublicKeySeed(String key) {
        long seed = 0;

        String keyHash = Hex.md5Hash(key);

        for(char s : keyHash.toCharArray()){
            seed += s;
        }

        return seed;
    }
}
