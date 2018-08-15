package im.adamant.android.ui;

import im.adamant.android.R;
import im.adamant.android.ui.mvp_view.CompanionDetailView;

public class CompanionDetailScreen extends BaseActivity implements CompanionDetailView {

    @Override
    public int getLayoutId() {
        return R.layout.activity_companion_detail_screen;
    }

    @Override
    public boolean withBackButton() {
        return true;
    }
}
