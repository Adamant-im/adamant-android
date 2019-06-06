package im.adamant.android.helpers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewAnimationUtils;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import im.adamant.android.R;

public class AnimationUtils {
    public interface AnimationFinishedListener {
        void onAnimationFinished();
    }

    public static int getMediumDuration(Context context) {
        int duration;
        if (isAnimationEnabled()) {
            duration = context.getResources().getInteger(android.R.integer.config_mediumAnimTime);
        } else {
            duration = 0;
        }
        return duration;
    }

    //For the Calabash tests we don't want to waste any time on animations!
    public static boolean isAnimationEnabled() {
        return true; //!BuildConfig.OFFLINE_TESTING;
    }

    @ColorInt
    private static int getColor(Context context, @ColorRes int colorId) {
        return ContextCompat.getColor(context, colorId);
    }

    private static void startCircularRevealAnimation(final Context context, final View view, final RevealAnimationSetting revealSettings, final int startColor, final int endColor, final AnimationFinishedListener listener) {
        if (isAnimationEnabled() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    v.removeOnLayoutChangeListener(this);

                    int cx = revealSettings.getCenterX();
                    int cy = revealSettings.getCenterY();
                    int width = revealSettings.getWidth();
                    int height = revealSettings.getHeight();

                    //Simply use the diagonal of the view
                    float finalRadius = (float) Math.sqrt(width * width + height * height);
                    Animator anim = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, finalRadius);
                    anim.setDuration(getMediumDuration(context));
                    anim.setInterpolator(new FastOutSlowInInterpolator());
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            listener.onAnimationFinished();
                        }
                    });
                    anim.start();
                    startBackgroundColorAnimation(view, startColor, endColor, getMediumDuration(context));
                }
            });
        } else {
            listener.onAnimationFinished();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void startCircularRevealExitAnimation(Context context, final View view, RevealAnimationSetting revealSettings, int startColor, int endColor, final AnimationFinishedListener listener) {
        if (isAnimationEnabled() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int cx = revealSettings.getCenterX();
            int cy = revealSettings.getCenterY();
            int width = revealSettings.getWidth();
            int height = revealSettings.getHeight();

            float initRadius = (float) Math.sqrt(width * width + height * height);
            Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initRadius, 0);
            anim.setDuration(getMediumDuration(context));
            anim.setInterpolator(new FastOutSlowInInterpolator());
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    //Important: This will prevent the view's flashing (visible between the finished animation and the Fragment remove)
                    view.setVisibility(View.GONE);
                    listener.onAnimationFinished();
                }
            });
            anim.start();
            startBackgroundColorAnimation(view, startColor, endColor, getMediumDuration(context));
        } else {
            listener.onAnimationFinished();
        }
    }

    private static void startBackgroundColorAnimation(final View view, int startColor, int endColor, int duration) {
        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(startColor, endColor);
        anim.setEvaluator(new ArgbEvaluator());
        anim.setDuration(duration);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setBackgroundColor((Integer) valueAnimator.getAnimatedValue());
            }
        });
        anim.start();
    }

    //Specific cases for our share link screen

    public static void startCreateChatRevealShowAnimation(Context context, View view, RevealAnimationSetting revealSettings, AnimationFinishedListener listener) {
        startCircularRevealAnimation(context, view, revealSettings, getColor(context, R.color.secondary), getColor(context, R.color.primary), listener);
    }

    public static void startCreateChatRevealExitAnimation(Context context, View view, RevealAnimationSetting revealSettings, AnimationFinishedListener listener) {
        startCircularRevealExitAnimation(context, view, revealSettings, getColor(context, R.color.primary), getColor(context, R.color.secondary), listener);
    }

    //We use this to remove the Fragment only when the animation finished
    public interface Dismissible {
        void dismiss(AnimationFinishedListener listener);
    }


    public static class RevealAnimationSetting implements Parcelable {
        private int centerX;
        private int centerY;
        private int width;
        private int height;

        public RevealAnimationSetting(int centerX, int centerY, int width, int height) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.width = width;
            this.height = height;
        }

        protected RevealAnimationSetting(Parcel in) {
            centerX = in.readInt();
            centerY = in.readInt();
            width = in.readInt();
            height = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(centerX);
            dest.writeInt(centerY);
            dest.writeInt(width);
            dest.writeInt(height);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<RevealAnimationSetting> CREATOR = new Creator<RevealAnimationSetting>() {
            @Override
            public RevealAnimationSetting createFromParcel(Parcel in) {
                return new RevealAnimationSetting(in);
            }

            @Override
            public RevealAnimationSetting[] newArray(int size) {
                return new RevealAnimationSetting[size];
            }
        };

        public static RevealAnimationSetting with(int centerX, int centerY, int width, int height) {
            return new RevealAnimationSetting(centerX, centerY, width, height);
        }

        public int getCenterX() {
            return centerX;
        }

        public void setCenterX(int centerX) {
            this.centerX = centerX;
        }

        public int getCenterY() {
            return centerY;
        }

        public void setCenterY(int centerY) {
            this.centerY = centerY;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }
}
