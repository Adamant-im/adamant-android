package im.adamant.android.ui;

import android.os.Bundle;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import javax.inject.Inject;
import javax.inject.Provider;

import dagger.android.AndroidInjection;
import im.adamant.android.R;
import im.adamant.android.ui.mvp_view.ShowQrCodeView;
import im.adamant.android.ui.presenters.ShowQrCodePresenter;

public class ShowQrCodeScreen extends BaseActivity implements ShowQrCodeView {
    public static final String ARG_DATA_FOR_QR_CODE = "data";

    @Inject
    Provider<ShowQrCodePresenter> presenterProvider;

    //--Moxy
    @InjectPresenter
    ShowQrCodePresenter presenter;

    @ProvidePresenter
    public ShowQrCodePresenter getPresenter(){
        return presenterProvider.get();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_show_qr_code_screen;
    }

    @Override
    public boolean withBackButton() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
    }
}
