package im.adamant.android.helpers;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

public class DrawableColorHeleper {
    public static void changeColorForDrawable(Context context, TextView textView, int color, PorterDuff.Mode mode) {
        for (Drawable drawable : textView.getCompoundDrawablesRelative()) {
            if (drawable != null) {
                drawable.setColorFilter(
                        new PorterDuffColorFilter(
                                ContextCompat.getColor(context, color),
                                mode
                        )
                );

            }
        }

        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(
                        new PorterDuffColorFilter(
                                ContextCompat.getColor(context, color),
                                mode
                        )
                );
            }
        }
    }
}
