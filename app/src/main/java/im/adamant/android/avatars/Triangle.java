package im.adamant.android.avatars;

import android.util.SparseArray;

import static im.adamant.android.avatars.Triangle.Direction.LEFT;
import static im.adamant.android.avatars.Triangle.Direction.RIGHT;


public class Triangle {

    private static final SparseArray<Integer[]> rotationsCache = new SparseArray<>();

    static {
        rotationsCache.append(0, new Integer[]{0, 6, 8, 8, 2, 0});
        rotationsCache.append(1, new Integer[]{1, 2, 5, 7, 6, 3});
        rotationsCache.append(2, new Integer[]{2, 0, 0, 6, 8, 8});
        rotationsCache.append(3, new Integer[]{3, 4, 7, 5, 4, 1});
        rotationsCache.append(4, new Integer[]{4, 1, 3, 4, 7, 5});
        rotationsCache.append(5, new Integer[]{5, 7, 6, 3, 1, 2});
        rotationsCache.append(6, new Integer[]{6, 3, 1, 2, 5, 7});
        rotationsCache.append(7, new Integer[]{7, 5, 4, 1, 3, 4});
        rotationsCache.append(8, new Integer[]{8, 8, 2, 0, 0, 6});
    }

    public static final Triangle[][] triangles = new Triangle[][]{
        {
            new Triangle(0, 1, RIGHT),
            new Triangle(0, 2, RIGHT),
            new Triangle(0, 3, RIGHT),
            new Triangle(0, 2, LEFT),
            new Triangle(0, 3, LEFT),
            new Triangle(1, 2, RIGHT),
            new Triangle(1, 3, RIGHT),
            new Triangle(1, 2, LEFT),
            new Triangle(2, 2, RIGHT)
        },
        {
            new Triangle(0, 1, LEFT),
            new Triangle(1, 1, RIGHT),
            new Triangle(1, 0, LEFT),
            new Triangle(1, 1, LEFT),
            new Triangle(2, 0, RIGHT),
            new Triangle(2, 1, RIGHT),
            new Triangle(2, 0, LEFT),
            new Triangle(2, 1, LEFT),
            new Triangle(2, 2, LEFT)
        },
        {
            new Triangle(3, 0, RIGHT),
            new Triangle(3, 1, RIGHT),
            new Triangle(3, 2, RIGHT),
            new Triangle(3, 0, LEFT),
            new Triangle(3, 1, LEFT),
            new Triangle(4, 0, RIGHT),
            new Triangle(4, 1, RIGHT),
            new Triangle(4, 1, LEFT),
            new Triangle(5, 1, RIGHT)
        },
        {
            new Triangle(3, 2, LEFT),
            new Triangle(4, 2, RIGHT),
            new Triangle(4, 2, LEFT),
            new Triangle(4, 3, LEFT),
            new Triangle(5, 2, RIGHT),
            new Triangle(5, 3, RIGHT),
            new Triangle(5, 1, LEFT),
            new Triangle(5, 2, LEFT),
            new Triangle(5, 3, LEFT)
        },
        {
            new Triangle(3, 3, RIGHT),
            new Triangle(3, 4, RIGHT),
            new Triangle(3, 5, RIGHT),
            new Triangle(3, 3, LEFT),
            new Triangle(3, 4, LEFT),
            new Triangle(4, 3, RIGHT),
            new Triangle(4, 4, RIGHT),
            new Triangle(4, 4, LEFT),
            new Triangle(5, 4, RIGHT)
        },
        {
            new Triangle(0, 4, LEFT),
            new Triangle(1, 4, RIGHT),
            new Triangle(1, 3, LEFT),
            new Triangle(1, 4, LEFT),
            new Triangle(2, 3, RIGHT),
            new Triangle(2, 4, RIGHT),
            new Triangle(2, 3, LEFT),
            new Triangle(2, 4, LEFT),
            new Triangle(2, 5, LEFT)
        }
    };


    public enum Direction {
        LEFT,
        RIGHT
    }

    private int x;
    private int y;
    private Direction direction;

    public Triangle(int x, int y, Direction direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean isInTriangle() {
        return this.triangleID() != -1;
    }

    // triangleID returns the triangle id (from 0 to 5)
    // that has a match with the position given as param.
    // returns -1 if a match is not found.
    public int triangleID() {
        for (int i = 0; i < Triangle.triangles.length; i++){
            Triangle[] t = Triangle.triangles[i];
            for (int ti = 0; ti < t.length; ti++){
                Triangle triangle = t[ti];
                if (triangle.getX() == this.x && triangle.getY() == this.y && this.direction == triangle.getDirection()) {
                    return i;
                }
            }
        }

        return -1;
    }

    // subTriangleID returns the sub triangle id (from 0 to 8)
    // that has a match with the position given as param.
    // returns -1 if a match is not found.
    public int subTriangleID() {
        for (int i = 0; i < Triangle.triangles.length; i++){
            Triangle[] t = Triangle.triangles[i];
            for (int ti = 0; ti < t.length; ti++){
                Triangle triangle = t[ti];
                if (triangle.getX() == this.x && triangle.getY() == this.y && this.direction == triangle.getDirection()) {
                    return ti;
                }
            }
        }

        return -1;
    }

    public Integer[] subTriangleRotations(int lookforSubTriangleID) {
        return rotationsCache.get(lookforSubTriangleID);
    }

    // rotationId returns the original sub triangle id
    // if the current triangle was rotated to position 0.
    public int rotationID() {
        int currentTID = this.triangleID();
        int currentSTID = this.subTriangleID();
        int numberOfSubTriangles = 9;
        for (int i = 0; i < numberOfSubTriangles; i++) {
            Integer[] rotations = subTriangleRotations(i);
            if (rotations != null) {
                if (rotations[currentTID] == currentSTID) {
                    return i;
                }
            }
        }
        return -1;
    }
}
