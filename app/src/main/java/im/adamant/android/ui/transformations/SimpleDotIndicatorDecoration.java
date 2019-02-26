package im.adamant.android.ui.transformations;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.yarolegovich.discretescrollview.DiscreteScrollView;

import org.spongycastle.asn1.cms.PasswordRecipientInfo;

import androidx.recyclerview.widget.RecyclerView;

public class SimpleDotIndicatorDecoration extends RecyclerView.ItemDecoration {
    private int activeColor = 0xFFFFFFFF;
    private int inactiveColor = 0x66FFFFFF;

    private static final float DP = Resources.getSystem().getDisplayMetrics().density;

    /**
     * Height of the space the indicator takes up at the bottom of the view.
     */
    private final int mIndicatorHeight = (int) (DP * 8);

    /**
     * Indicator width.
     */
    private final float mIndicatorItemLength = DP * 8;
    /**
     * Padding between indicators.
     */
    private final float mIndicatorItemPadding = DP * 8;

    private float bottomPadding = 0.0f;


    private final Paint mPaint = new Paint();

    public SimpleDotIndicatorDecoration(int activeColor, int inactiveColor, int padding) {
        this.activeColor = activeColor;
        this.inactiveColor = inactiveColor;
        this.bottomPadding = padding * DP;
        mPaint.setAntiAlias(true);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        int itemCount = parent.getAdapter().getItemCount();

        // center horizontally, calculate width and subtract half from center
        float totalLength = mIndicatorItemLength * itemCount;
        float paddingBetweenItems = Math.max(0, itemCount - 1) * mIndicatorItemPadding;
        float indicatorTotalWidth = totalLength + paddingBetweenItems;
        float indicatorStartX = (parent.getWidth() - indicatorTotalWidth) / 2F;

        // center vertically in the allotted space
        float indicatorPosY = parent.getHeight() - ((mIndicatorHeight / 2F) + bottomPadding);

        drawInactiveIndicators(c, indicatorStartX, indicatorPosY, itemCount);


        // find active page (which should be highlighted)
        DiscreteScrollView recycler = (DiscreteScrollView) parent;
        int activePosition = recycler.getCurrentItem();
        if (activePosition == RecyclerView.NO_POSITION) {
            return;
        }

        drawHighlights(c, indicatorStartX, indicatorPosY, activePosition);
    }

    private void drawInactiveIndicators(Canvas c, float indicatorStartX, float indicatorPosY, int itemCount) {
        mPaint.setColor(inactiveColor);

        // width of item indicator including padding
        final float itemWidth = mIndicatorItemLength + mIndicatorItemPadding;

        float start = indicatorStartX;
        for (int i = 0; i < itemCount; i++) {
            // draw the line for every item
            c.drawCircle(start + (mIndicatorItemLength / 2), indicatorPosY, (mIndicatorItemLength / 2), mPaint);
            start += itemWidth;
        }
    }

    private void drawHighlights(Canvas c, float indicatorStartX, float indicatorPosY,
                                     int highlightPosition) {
        mPaint.setColor(activeColor);

        // width of item indicator including padding
        final float itemWidth = mIndicatorItemLength + mIndicatorItemPadding;

        // no swipe, draw a normal indicator
        float highlightStart = indicatorStartX + itemWidth * highlightPosition;
        c.drawCircle(highlightStart + (mIndicatorItemLength / 2), indicatorPosY, (mIndicatorItemLength / 2), mPaint);
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = mIndicatorHeight;
    }
}
