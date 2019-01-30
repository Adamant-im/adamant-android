package im.adamant.android.helpers;

import android.os.Build;
import android.text.Html;
import android.text.Spanned;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

public class HtmlHelper {
    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }
}
