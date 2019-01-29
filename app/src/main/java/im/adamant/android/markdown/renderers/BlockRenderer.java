package im.adamant.android.markdown.renderers;

import java.util.List;

import androidx.annotation.NonNull;
import im.adamant.android.markdown.renderers.block.BlockDescription;

public interface BlockRenderer {
    @NonNull
    String getContentBlock(String s);
    String renderBlock(String s);
}
