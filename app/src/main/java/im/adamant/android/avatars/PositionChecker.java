package im.adamant.android.avatars;

@FunctionalInterface
public interface PositionChecker {
    boolean isIt(int coordinate);
}
