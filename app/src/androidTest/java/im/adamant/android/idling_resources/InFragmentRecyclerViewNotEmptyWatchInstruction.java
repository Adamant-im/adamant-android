package im.adamant.android.idling_resources;

import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.azimolabs.conditionwatcher.Instruction;

import im.adamant.android.R;
import im.adamant.android.ui.fragments.ChatsScreen;

public class InFragmentRecyclerViewNotEmptyWatchInstruction extends Instruction {
    protected FragmentManager fragmentManager;
    protected String fragmentTag;
    protected int recyclerViewResource;

    protected RecyclerView loadedRecyclerView;

    public InFragmentRecyclerViewNotEmptyWatchInstruction(
            FragmentManager fragmentManager,
            String fragmentTag,
            int recyclerViewResource
    ) {
        this.fragmentManager = fragmentManager;
        this.fragmentTag = fragmentTag;
        this.recyclerViewResource = recyclerViewResource;
    }

    @Override
    public String getDescription() {
        return "RecyclerView is empty!";
    }

    @Override
    public boolean checkCondition() {
        ChatsScreen screen = (ChatsScreen) fragmentManager.findFragmentByTag(fragmentTag);
        if (screen == null) { return false; }
        View view = screen.getView();

        if (view == null) { return false; }
        loadedRecyclerView = (RecyclerView)view.findViewById(recyclerViewResource);

        if (loadedRecyclerView == null) { return false; }
        RecyclerView.Adapter adapter = loadedRecyclerView.getAdapter();

        if (adapter == null) { return false; }
        return adapter.getItemCount() > 0;

    }
}
