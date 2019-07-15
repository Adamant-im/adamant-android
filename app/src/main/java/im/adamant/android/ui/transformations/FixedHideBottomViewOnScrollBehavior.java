package im.adamant.android.ui.transformations;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;

public class FixedHideBottomViewOnScrollBehavior<V extends View> extends HideBottomViewOnScrollBehavior<V> {

    public FixedHideBottomViewOnScrollBehavior() {
    }

    public FixedHideBottomViewOnScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, V child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        boolean callSuper = true;
        if (target instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) target;
            int estimatedHeight = recyclerView.computeVerticalScrollRange();
            int totalSpace = child.getHeight() + target.getHeight();
            if (estimatedHeight <= totalSpace) {
                callSuper = false;
            }
        }
        if (callSuper) {
            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed,
                    dxUnconsumed, dyUnconsumed);
        }
    }
}
