package im.adamant.android.ui;

import im.adamant.android.R;

public class RegistrationScreen extends BaseActivity {
    @Override
    public int getLayoutId() {
        return R.layout.activity_registration_screen;
    }

    @Override
    public boolean withBackButton() {
        return false;
    }
}
