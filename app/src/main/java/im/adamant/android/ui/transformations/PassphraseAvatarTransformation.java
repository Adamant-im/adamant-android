package im.adamant.android.ui.transformations;

import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import com.yarolegovich.discretescrollview.transform.DiscreteScrollItemTransformer;
import com.yarolegovich.discretescrollview.transform.Pivot;

import im.adamant.android.R;
import im.adamant.android.helpers.LoggerHelper;

public class PassphraseAvatarTransformation implements DiscreteScrollItemTransformer {
    private static final float SELECTED_TRANSLATION_Z = 35.0f;

    private Pivot pivotX;
    private Pivot pivotY;
    private float minScale;
    private float maxMinDiff;

    public PassphraseAvatarTransformation() {
        pivotX = Pivot.X.CENTER.create();
        pivotY = Pivot.Y.BOTTOM.create();
        minScale = 0.8f;
        maxMinDiff = 0.25f;
    }

    @Override
    public void transformItem(View item, float position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ImageView avatarView = item.findViewById(R.id.list_item_passphrase_avatar);

            float translationZ = (float) Math.abs(SELECTED_TRANSLATION_Z - ((Math.pow(2, Math.abs(position)) - 1) * SELECTED_TRANSLATION_Z));
            avatarView.setTranslationZ(translationZ);
        }

        pivotX.setOn(item);
        pivotY.setOn(item);
        float closenessToCenter = 1f - Math.abs(position);
        float scale = minScale + maxMinDiff * closenessToCenter;
        item.setScaleX(scale);
        item.setScaleY(scale);
    }

}
