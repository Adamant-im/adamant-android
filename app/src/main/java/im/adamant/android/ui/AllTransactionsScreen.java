package im.adamant.android.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import dagger.android.AndroidInjection;
import im.adamant.android.R;
import im.adamant.android.Screens;
import im.adamant.android.ui.adapters.CurrencyTransfersAdapter;
import im.adamant.android.ui.custom_view.EndlessRecyclerViewScrollListener;
import im.adamant.android.ui.entities.CurrencyTransferEntity;
import im.adamant.android.ui.mvp_view.AllTransactionsView;
import im.adamant.android.ui.navigators.DefaultNavigator;
import im.adamant.android.ui.presenters.AllTransactionsPresenter;
import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.commands.Back;
import ru.terrakok.cicerone.commands.BackTo;
import ru.terrakok.cicerone.commands.Forward;
import ru.terrakok.cicerone.commands.Replace;
import ru.terrakok.cicerone.commands.SystemMessage;

public class AllTransactionsScreen extends BaseActivity implements AllTransactionsView {
    public static final String ARG_CURRENCY_ABBR = "CURRENCY_ABBR";

    @Inject
    NavigatorHolder navigatorHolder;

    @Inject
    Provider<AllTransactionsPresenter> presenterProvider;

    //--Moxy
    @InjectPresenter
    AllTransactionsPresenter presenter;

    @BindView(R.id.activity_all_transaction_rv_transactions)
    RecyclerView transactionsView;

    @Inject
    CurrencyTransfersAdapter currencyTransfersAdapter;
    @BindView(R.id.progress)
    View progressBar;

    @ProvidePresenter
    public AllTransactionsPresenter getPresenter() {
        return presenterProvider.get();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_all_transactions_screen;
    }

    @Override
    public boolean withBackButton() {
        return true;
    }

    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        currencyTransfersAdapter.setOnClickedLister(presenter::onTransactionClicked);

        layoutManager = new LinearLayoutManager(this);
        transactionsView.setLayoutManager(layoutManager);
        transactionsView.setAdapter(currencyTransfersAdapter);


        Drawable divider = ContextCompat.getDrawable(transactionsView.getContext(), R.drawable.line_divider);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(transactionsView.getContext(), layoutManager.getOrientation());

        if (divider != null) {
            dividerItemDecoration.setDrawable(divider);
        }

        transactionsView.addItemDecoration(dividerItemDecoration);


        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(ARG_CURRENCY_ABBR)) {
                String abbr = getIntent().getStringExtra(ARG_CURRENCY_ABBR);
                String pattern = getString(R.string.activity_all_transactions_title);
                String title = String.format(Locale.ENGLISH, pattern, abbr);
                setTitle(title);

                if (savedInstanceState == null) {
                    presenter.onShowTransactionsByCurrencyAbbr(abbr);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigatorHolder.setNavigator(navigator);

        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                presenter.onLoadNextTransfers();
            }
        };
        transactionsView.addOnScrollListener(endlessRecyclerViewScrollListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        navigatorHolder.removeNavigator();
    }

    private Navigator navigator = new DefaultNavigator(this) {
        @Override
        protected void forward(Forward forwardCommand) {
            switch (forwardCommand.getScreenKey()){
                case Screens.TRANSFER_DETAILS_SCREEN: {
                    Bundle bundle = (Bundle) forwardCommand.getTransitionData();
                    Intent intent = new Intent(getApplicationContext(), TransferDetailsScreen.class);
                    intent.putExtras(bundle);

                    startActivity(intent);
                }
                break;
            }
        }

        @Override
        protected void back(Back backCommand) {

        }

        @Override
        protected void backTo(BackTo backToCommand) {

        }

        @Override
        protected void message(SystemMessage systemMessageCommand) {
            Toast.makeText(getApplicationContext(), systemMessageCommand.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        protected void replace(Replace command) {

        }
    };

    @Override
    public void firstTransfersWasLoaded(List<CurrencyTransferEntity> transfers) {
        transfers = new ArrayList<>(transfers);
        currencyTransfersAdapter.refreshItems(transfers);
        if (endlessRecyclerViewScrollListener != null) {
            endlessRecyclerViewScrollListener.resetState();
        }
    }

    @Override
    public void newTransferWasLoaded(CurrencyTransferEntity transfer) {
        currencyTransfersAdapter.addItemToBegin(transfer);
    }

    @Override
    public void setLoading(boolean loading) {
        if (loading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void nextTransferWasLoaded(CurrencyTransferEntity transfer) {
        currencyTransfersAdapter.addItemToEnd(transfer);
    }
}
