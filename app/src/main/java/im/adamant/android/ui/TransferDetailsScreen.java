package im.adamant.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import im.adamant.android.R;
import im.adamant.android.ui.mvp_view.TransferDetailsView;
import im.adamant.android.ui.presenters.TransferDetailsPresenter;

public class TransferDetailsScreen extends BaseActivity implements TransferDetailsView {
    public static final String TRANSFER_ID_KEY = "transer_id";
    public static final String CURRENCY_ABBR = "currency";

    @BindView(R.id.amount) TextView amount;
    @BindView(R.id.status) TextView status;
    @BindView(R.id.date) TextView date;
    @BindView(R.id.confirmations) TextView confirmations;
    @BindView(R.id.fee) TextView fee;
    @BindView(R.id.from) TextView from;
    @BindView(R.id.to) TextView to;
    @BindView(R.id.explorerGroup) View explorerGroup;
    @BindView(R.id.chatGroup) View chatGroup;
    @BindView(R.id.chatLabel) TextView chatLabel;

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

    private void initTitle() {
        titleView.setMaxLines(1);
        titleView.setEllipsize(TextUtils.TruncateAt.END);
        setTitle(transferId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadArgs();
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            presenter.initParams(transferId, currencyAbbr);
        }
        ButterKnife.bind(this);
        initTitle();
        explorerGroup.setOnClickListener(v -> presenter.showExplorerClicked());
        chatGroup.setOnClickListener(v -> presenter.chatClicked());
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_transfer_details_screen;
    }

    @Override
    public boolean withBackButton() {
        return true;
    }

    private String getFrom(UITransferDetails details){
        StringBuilder sb = new StringBuilder();
        if(details.getFromAddress() != null) {
            sb.append(details.getFromAddress());
            sb.append("\n");
        }
        sb.append(details.getFromId());
        return sb.toString();
    }

    private String getTo(UITransferDetails details) {
        StringBuilder sb = new StringBuilder();
        if (details.getToAddress() != null) {
            sb.append(details.getToAddress());
            sb.append("\n");
        }
        sb.append(details.getToId());
        return sb.toString();
    }


    @Override
    public void showTransferDetails(UITransferDetails details) {
        amount.setText(details.getAmount());
        status.setText(details.getStatus());
        date.setText(details.getDate());
        confirmations.setText(String.format(Locale.getDefault(), "%d", details.getConfirmations()));
        fee.setText(details.getFee());
        from.setText(getFrom(details));
        to.setText(getTo(details));
        if (details.haveChat()) {
            chatLabel.setText(R.string.activity_transfer_details_continue_chat);
        } else {
            chatLabel.setText(R.string.activity_transfer_details_start_chat);
        }
    }

    private static final String LOADING_CHAR = "\u231B";

    @Override
    public void setLoading(boolean loading) {
        if (loading) {
            amount.setText(LOADING_CHAR);
            status.setText(LOADING_CHAR);
            date.setText(LOADING_CHAR);
            confirmations.setText(LOADING_CHAR);
            from.setText(LOADING_CHAR);
            to.setText(LOADING_CHAR);
            fee.setText(LOADING_CHAR);
            explorerGroup.setVisibility(View.GONE);
            chatGroup.setVisibility(View.GONE);
        } else {
            explorerGroup.setVisibility(View.VISIBLE);
            chatGroup.setVisibility(View.VISIBLE);
        }
    }
}
