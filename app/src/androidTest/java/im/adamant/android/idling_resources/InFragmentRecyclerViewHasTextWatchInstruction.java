package im.adamant.android.idling_resources;

import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import javax.annotation.Nonnull;

public class InFragmentRecyclerViewHasTextWatchInstruction extends InFragmentRecyclerViewNotEmptyWatchInstruction {
    private int position;
    private int targetTextViewResource;
    private String expectedText;

    public InFragmentRecyclerViewHasTextWatchInstruction(
            FragmentManager fragmentManager,
            String fragmentTag,
            int recyclerViewResource,
            int targetTextViewResource,
            int position,
            @Nonnull String expectedText
    ) {
        super(fragmentManager, fragmentTag, recyclerViewResource);
        this.position = position;
        this.expectedText = expectedText;
        this.targetTextViewResource = targetTextViewResource;
    }

    @Override
    public String getDescription() {
        return super.getDescription();
    }

    @Override
    public boolean checkCondition() {
        boolean isLoadedItems = super.checkCondition();

        if (!isLoadedItems) { return false; }

        RecyclerView.ViewHolder holder = loadedRecyclerView.findViewHolderForAdapterPosition(position);
        if (holder == null) { return false; }

        TextView textView = holder.itemView.findViewById(targetTextViewResource);
        if (textView == null) { return false; }

        return expectedText.equalsIgnoreCase(textView.getText().toString());
    }
}
