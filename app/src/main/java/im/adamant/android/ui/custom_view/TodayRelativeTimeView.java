package im.adamant.android.ui.custom_view;

import android.content.Context;
import android.util.AttributeSet;

import com.github.curioustechizen.ago.RelativeTimeTextView;

import java.util.Calendar;

import im.adamant.android.R;

public class TodayRelativeTimeView extends RelativeTimeTextView {
    private Calendar calendar = Calendar.getInstance();
    private boolean isToday;

    public TodayRelativeTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected CharSequence getRelativeTimeDisplayString(long referenceTime, long now) {
        calendar.setTimeInMillis(now);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long startTodayTimestamp = calendar.getTimeInMillis();

        calendar.setTimeInMillis(referenceTime);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long startReferenceDayTimestamp = calendar.getTimeInMillis();

        if (startReferenceDayTimestamp == startTodayTimestamp){
            isToday = true;
            return getResources().getString(R.string.today);
        } else {
            isToday = false;
            return super.getRelativeTimeDisplayString(referenceTime, now);
        }
    }

    public boolean isToday() {
        return isToday;
    }
}
