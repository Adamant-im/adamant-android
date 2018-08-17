package im.adamant.android.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import im.adamant.android.R;
import im.adamant.android.presenters.CompanionDetailPresenter;
import im.adamant.android.services.SaveContactsService;
import im.adamant.android.ui.mvp_view.CompanionDetailView;
import ru.terrakok.cicerone.NavigatorHolder;

public class CompanionDetailScreen extends BaseActivity implements CompanionDetailView {
    public final static String ARG_COMPANION_ID = "companion_id";

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
    @BindView(R.id.activity_companion_detail_et_name) EditText companionName;


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

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(ARG_COMPANION_ID)) {
                String companion = getIntent().getStringExtra(ARG_COMPANION_ID);
                presenter.onLoadInfoByChat(companion);
            }
        }
    }

    @Override
    public void showCompanionName(String name) {
        companionName.setText(name);
    }

    @Override
    public void startSavingContacts() {
        Intent intent = new Intent(getApplicationContext(), SaveContactsService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }

        finish();
    }

    @OnClick(R.id.activity_companion_detail_btn_rename)
    public void onClickRenameButton() {
        presenter.onClickRenameButton(companionName.getText().toString());
    }
}
