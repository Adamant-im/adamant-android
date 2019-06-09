package im.adamant.android.helpers;

import android.graphics.drawable.Drawable;
import android.util.LayoutDirection;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public abstract class DrawableClickListener implements View.OnTouchListener {
    private static final int DRAWABLE_START = 0;
    private static final int DRAWABLE_TOP = 1;
    private static final int DRAWABLE_END = 2;
    private static final int DRAWABLE_BOTTOM = 3;

    private Class<? extends EditText> viewType;

    public DrawableClickListener(Class<? extends EditText> viewType) {
        this.viewType = viewType;
    }

    //TODO: Accessibility and RTL directions
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!viewType.isInstance(v)) { return false; }

        EditText editableView = viewType.cast(v);

        if(editableView != null && event.getAction() == MotionEvent.ACTION_UP) {
            Drawable drawableStart = editableView.getCompoundDrawablesRelative()[DRAWABLE_START];
            Drawable drawableEnd = editableView.getCompoundDrawablesRelative()[DRAWABLE_END];

            if (drawableEnd == null && drawableStart == null) {
                return false;
            }

            boolean isStartClicked = false;
            boolean isEndClicked = false;

            int layoutDirection = editableView.getLayoutDirection();
            if (layoutDirection == LayoutDirection.LTR) {
                isStartClicked = detectLeftClick(editableView, drawableStart, event);
                isEndClicked = detectRightClick(editableView, drawableStart, event);
            } else {
                isStartClicked = detectRightClick(editableView, drawableStart, event);
                isEndClicked = detectLeftClick(editableView, drawableStart, event);
            }

            if (isStartClicked) {
                onClickStartDrawable(v);
                return true;
            }

            if (isEndClicked) {
                onClickEndDrawable(v);
                return true;
            }

        }
        return false;
    }

    protected abstract void onClickStartDrawable(View v);
    protected abstract void onClickEndDrawable(View v);

    private boolean detectLeftClick(EditText editableView, Drawable drawable, MotionEvent event) {
        return (drawable != null) && (event.getX() <= editableView.getLeft() + editableView.getPaddingLeft() + drawable.getBounds().width());
    }

    private boolean detectRightClick(EditText editableView, Drawable drawable, MotionEvent event) {
        return (drawable != null) && (event.getX() >= (editableView.getRight() - (drawable.getBounds().width() + editableView.getPaddingRight())));
    }
}
