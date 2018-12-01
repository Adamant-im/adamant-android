package im.adamant.android.avatars;

import android.graphics.Color;

public class AvatarThemesProvider {
    private static final int[][] colors = new int[][]{
            {
                    Color.parseColor("#ffffff"), //background
                    Color.parseColor("#179cec"), // main
                    Color.parseColor("#8bcef6"), // 2dary
                    Color.parseColor("#c5e6fa") // 2dary
            },
            {
                    Color.parseColor("#ffffff"), //background
                    Color.parseColor("#32d296"), // main
                    Color.parseColor("#99e9cb"), // 2dary
                    Color.parseColor("#ccf4e5") // 2dary
            },
            {
                    Color.parseColor("#ffffff"), //background
                    Color.parseColor("#faa05a"), // main
                    Color.parseColor("#fdd0ad"), // 2dary
                    Color.parseColor("#fee7d6") // 2dary
            },
            {
                    Color.parseColor("#ffffff"), //background
                    Color.parseColor("#474a5f"), // main
                    Color.parseColor("#a3a5af"), // 2dary
                    Color.parseColor("#d1d2d7") // 2dary
            },
            {
                    Color.parseColor("#ffffff"), //background
                    Color.parseColor("#9497a3"), // main
                    Color.parseColor("#cacbd1"), // 2dary
                    Color.parseColor("#e4e5e8") // 2dary
            }
    };

    public int[] provide(int index) {
        if (index >= 0 && index < colors.length){
            return colors[index];
        } else {
            return colors[0];
        }
    }

    public int provideBorderColor() {
        return Color.parseColor("#cacbd1");
    }

    public int count() {
        return colors.length;
    }
}
