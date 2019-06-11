package im.adamant.android.ui.transformations;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import im.adamant.android.R;
import im.adamant.android.helpers.LoggerHelper;

public class FragmentContainerStickyBottomBehavior extends CoordinatorLayout.Behavior<FrameLayout> {
    public FragmentContainerStickyBottomBehavior() {
    }

    public FragmentContainerStickyBottomBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull FrameLayout child, @NonNull View dependency) {
        return dependency instanceof BottomNavigationView;
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull FrameLayout child, @NonNull View dependency) {
        int y = (int)dependency.getY();
        int parentBottom = parent.getBottom();
        int padding = parentBottom - y;
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
        params.setMargins(0, 0, 0, padding);
        child.setLayoutParams(params);

        return true;
    }
}
