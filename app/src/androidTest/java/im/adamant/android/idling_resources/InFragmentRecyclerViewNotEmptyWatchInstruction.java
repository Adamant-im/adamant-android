package im.adamant.android.idling_resources;

import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.azimolabs.conditionwatcher.Instruction;

import im.adamant.android.R;
import im.adamant.android.ui.fragments.ChatsScreen;

public class InFragmentRecyclerViewNotEmptyWatchInstruction extends Instruction {
    private FragmentManager fragmentManager;
    private String fragmentTag;

    public InFragmentRecyclerViewNotEmptyWatchInstruction(FragmentManager fragmentManager, String fragmentTag) {
        this.fragmentManager = fragmentManager;
        this.fragmentTag = fragmentTag;
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
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.fragment_chats_rv_chats);

        if (recyclerView == null) { return false; }
        RecyclerView.Adapter adapter = recyclerView.getAdapter();

        if (adapter == null) { return false; }
        return adapter.getItemCount() > 0;

    }
}
