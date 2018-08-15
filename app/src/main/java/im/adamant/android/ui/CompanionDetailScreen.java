package im.adamant.android.ui;

import android.os.Bundle;
import android.widget.EditText;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import butterknife.BindView;
import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.presenters.CompanionDetailPresenter;
import im.adamant.android.ui.mvp_view.CompanionDetailView;
import ru.terrakok.cicerone.NavigatorHolder;

public class CompanionDetailScreen extends BaseActivity implements CompanionDetailView {
    @Inject
    NavigatorHolder navigatorHolder;

    @Inject
    Provider<CompanionDetailPresenter> presenterProvider;

    //--Moxy
    @InjectPresenter
    CompanionDetailPresenter presenter;

    @ProvidePresenter
    public CompanionDetailPresenter getPresenter(){
        return presenterProvider.get();
    }

    //--ButterKnife
    @BindView(R.id.activity_create_chat_et_address) EditText companionName;


    @Override
    public int getLayoutId() {
        return R.layout.activity_companion_detail_screen;
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

    @Override
    public void showCompanionName(String name) {
        companionName.setText(name);
    }
}
