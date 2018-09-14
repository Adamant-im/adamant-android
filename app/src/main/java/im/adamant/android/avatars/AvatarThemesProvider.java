package im.adamant.android.avatars;

import android.graphics.Color;

public class AvatarThemesProvider {
    private static final int[][] colors = new int[][]{
            {
                    Color.parseColor("#ffffffff"), //background
                    Color.parseColor("#179cecff"), // main
                    Color.parseColor("#179cec80"), // 2dary
                    Color.parseColor("#179cec40") // 2dary
            },
            {
                    Color.parseColor("#ffffffff"), //background
                    Color.parseColor("#32d296ff"), // main
                    Color.parseColor("#32d29680"), // 2dary
                    Color.parseColor("#32d29640") // 2dary
            },
            {
                    Color.parseColor("#ffffffff"), //background
                    Color.parseColor("#faa05aff"), // main
                    Color.parseColor("#faa05a80"), // 2dary
                    Color.parseColor("#faa05a40") // 2dary
            },
            {
                    Color.parseColor("#ffffffff"), //background
                    Color.parseColor("#474a5fff"), // main
                    Color.parseColor("#474a5f80"), // 2dary
                    Color.parseColor("#474a5f40") // 2dary
            },
            {
                    Color.parseColor("#ffffffff"), //background
                    Color.parseColor("#9497a3ff"), // main
                    Color.parseColor("#9497a380"), // 2dary
                    Color.parseColor("#9497a340") // 2dary
            }
    };

    public int[] provide(int index) {
        if (index >= 0 && index < colors.length){
            return colors[index];
        } else {
            return colors[0];
        }
    }

    public int count() {
        return colors.length;
    }
}
