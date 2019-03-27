package im.adamant.android.ui.custom_view;


import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import im.adamant.android.R;

public class PinIndicatorLayout extends View {
    public static final String PROPERTY_RADIUS = "dotRadius";
    private int length = 10;
    private int[] radiuses = new int[length];
    private ValueAnimator[] fillAnimators = new ValueAnimator[length];
    private ValueAnimator[] deleteAnimators = new ValueAnimator[length];
    private int dotDiameterPx = 8;
    private int paddingSizePx = 8;
    private int animationDelay = 600;
    private int inactiveColor = Color.BLACK;
    private int activeColor = Color.WHITE;
    private Paint inactivePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint activePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int defaultDotRadiusPx = 0;
    private int baseline = 0;
    private int xStartPoint = 0;

    public PinIndicatorLayout(Context context) {
        super(context);
        init(null);
    }

    public PinIndicatorLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PinIndicatorLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PinIndicatorLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public void setSymbol(int index) {
        if ((index < 0) || (index >= fillAnimators.length)) { return; }

        deleteAnimators[index].cancel();
        fillAnimators[index].start();
    }

    public void removeSymbol(int index) {
        if ((index < 0) || (index >= deleteAnimators.length)) { return; }

        fillAnimators[index].cancel();
        deleteAnimators[index].start();
    }

    public void clear() {
        for (int index = 0; index < length; index++) {
            if (radiuses[index] != 0) {
                fillAnimators[index].cancel();
                deleteAnimators[index].start();
            }
        }
    }

    public int getLength() {
        return length;
    }

    private void init(@Nullable AttributeSet set) {
        if(set != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(set, R.styleable.PinIndicatorLayout);
            activeColor = typedArray.getColor(R.styleable.PinIndicatorLayout_active_color, Color.WHITE);
            inactiveColor = typedArray.getColor(R.styleable.PinIndicatorLayout_inactive_color, Color.BLACK);
            dotDiameterPx = typedArray.getDimensionPixelSize(R.styleable.PinIndicatorLayout_dot_diameter, 8);
            paddingSizePx = typedArray.getDimensionPixelSize(R.styleable.PinIndicatorLayout_padding_size, 8);
            animationDelay = typedArray.getInteger(R.styleable.PinIndicatorLayout_animation_delay, 600);
            length = typedArray.getInteger(R.styleable.PinIndicatorLayout_pincode_length, 10);
            inactivePaint.setColor(inactiveColor);
            inactivePaint.setStyle(Paint.Style.FILL);

            activePaint.setColor(activeColor);
            activePaint.setStyle(Paint.Style.FILL);

            typedArray.recycle();
        }


        for (int index = 0; index < length; index++) {
            final int bindedIndex = index;
            PropertyValuesHolder fillPropertyRadius = PropertyValuesHolder.ofInt(PROPERTY_RADIUS, 0, dotDiameterPx / 2);
            fillAnimators[bindedIndex] = new ValueAnimator();
            fillAnimators[bindedIndex].setValues(fillPropertyRadius);
            fillAnimators[bindedIndex].setInterpolator(new AccelerateDecelerateInterpolator());
            fillAnimators[bindedIndex].setDuration(animationDelay);
            fillAnimators[bindedIndex].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    radiuses[bindedIndex] = (int) animation.getAnimatedValue(PROPERTY_RADIUS);
                    invalidate();
                }
            });

            PropertyValuesHolder deletePropertyRadius = PropertyValuesHolder.ofInt(PROPERTY_RADIUS, dotDiameterPx / 2, 0);

            deleteAnimators[bindedIndex] = new ValueAnimator();
            deleteAnimators[bindedIndex].setValues(deletePropertyRadius);
            deleteAnimators[bindedIndex].setInterpolator(new AccelerateDecelerateInterpolator());
            deleteAnimators[bindedIndex].setDuration(animationDelay);
            deleteAnimators[bindedIndex].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    radiuses[bindedIndex] = (int) animation.getAnimatedValue(PROPERTY_RADIUS);
                    invalidate();
                }
            });
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        initCoordinates();
        for (int i = 0; i < length; i++) {
            canvas.drawCircle(getXCoordinateForItem(i), baseline, defaultDotRadiusPx, inactivePaint);
            canvas.drawCircle(getXCoordinateForItem(i), baseline,  radiuses[i], activePaint);
        }
    }

    private int getXCoordinateForItem(int itemIndex) {
        return xStartPoint + ((defaultDotRadiusPx + paddingSizePx + defaultDotRadiusPx) * itemIndex);
    }

    private void initCoordinates() {
        defaultDotRadiusPx = (dotDiameterPx / 2);
        baseline = getHeight() / 2;
        int activeAreaWidth = (dotDiameterPx * length) + (paddingSizePx * (length - 1));

        xStartPoint = (getWidth() / 2) - (activeAreaWidth / 2) + defaultDotRadiusPx;
    }
}
