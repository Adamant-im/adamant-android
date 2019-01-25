package im.adamant.android.markdown.renderers;

import java.util.List;

import im.adamant.android.markdown.renderers.block.BlockDescription;

public interface BlockRenderer {
    BlockDescription getNextBlock(StringBuilder s);
    String renderBlock(String s);
}
