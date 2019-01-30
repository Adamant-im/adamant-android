package im.adamant.android.markdown.renderers;

import java.util.List;

import androidx.annotation.NonNull;

public interface BlockRenderer {
    @NonNull
    String getContentBlock(String s);
    String renderBlock(String s);
}
