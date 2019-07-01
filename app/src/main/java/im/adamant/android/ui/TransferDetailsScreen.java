package im.adamant.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import javax.inject.Inject;
import javax.inject.Provider;

import dagger.android.AndroidInjection;
import im.adamant.android.R;
import im.adamant.android.ui.entities.TransferDetails;
import im.adamant.android.ui.mvp_view.TransferDetailsView;
import im.adamant.android.ui.presenters.TransferDetailsPresenter;

public class TransferDetailsScreen extends BaseActivity implements TransferDetailsView {
    public static final String TRANSFER_ID_KEY = "transer_id";
    public static final String CURRENCY_ABBR = "currency";

    @Inject
    Provider<TransferDetailsPresenter> presenterProvider;

    //--Moxy
    @InjectPresenter
    TransferDetailsPresenter presenter;

    @ProvidePresenter
    public TransferDetailsPresenter getPresenter() {
        return presenterProvider.get();
    }

    private String transferId, currencyAbbr;

    private void loadArgs() {
        Intent intent = getIntent();
        if (intent == null) {
            throw new IllegalArgumentException();
        }
        Bundle args = intent.getExtras();
        if (args == null) {
            throw new IllegalArgumentException();
        }
        transferId = args.getString(TRANSFER_ID_KEY);
        currencyAbbr = args.getString(CURRENCY_ABBR);
        if (transferId == null || currencyAbbr == null) {
            throw new IllegalArgumentException();
        }
    }

    private void initTitle(){
        titleView.setMaxLines(1);
        titleView.setEllipsize(TextUtils.TruncateAt.END);
        setTitle(transferId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadArgs();
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        presenter.setCurrencyAbbr(currencyAbbr);
        presenter.setTransactionId(transferId);
        initTitle();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_transfer_details_screen;
    }

    @Override
    public boolean withBackButton() {
        return true;
    }

    @Override
    public void showTransferDetails(TransferDetails details) {

    }
}
