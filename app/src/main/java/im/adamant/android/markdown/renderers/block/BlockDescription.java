package im.adamant.android.markdown.renderers.block;

public class BlockDescription {
    private String content;
    private int lenghtInOriginal;

    public BlockDescription(String content, int lenghtInOriginal) {
        this.content = content;
        this.lenghtInOriginal = lenghtInOriginal;
    }

    public String getContent() {
        return content;
    }

    public int getLenghtInOriginal() {
        return lenghtInOriginal;
    }
}
