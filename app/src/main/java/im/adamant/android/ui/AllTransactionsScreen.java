package im.adamant.android.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import dagger.android.AndroidInjection;
import im.adamant.android.R;
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

    @ProvidePresenter
    public AllTransactionsPresenter getPresenter(){
        return presenterProvider.get();
    }

    @Inject
    CurrencyTransfersAdapter currencyTransfersAdapter;

    @BindView(R.id.activity_all_transaction_rv_transactions) RecyclerView transactionsView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_all_transactions_screen;
    }

    @Override
    public boolean withBackButton() {
        return true;
    }

    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        transactionsView.setLayoutManager(layoutManager);
        transactionsView.setAdapter(currencyTransfersAdapter);

        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                presenter.onLoadNextTransfers();
            }
        };
        transactionsView.addOnScrollListener(endlessRecyclerViewScrollListener);


        Drawable divider = ContextCompat.getDrawable(transactionsView.getContext(), R.drawable.line_divider);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(transactionsView.getContext(), layoutManager.getOrientation());

        if (divider != null) {
            dividerItemDecoration.setDrawable(divider);
        }

        transactionsView.addItemDecoration(dividerItemDecoration);


        Intent intent = getIntent();
        if (intent != null){
            if (intent.hasExtra(ARG_CURRENCY_ABBR)){
                String abbr = getIntent().getStringExtra(ARG_CURRENCY_ABBR);
                String pattern = getString(R.string.activity_all_transactions_title);
                String title = String.format(Locale.ENGLISH, pattern, abbr);
                setTitle(title);

                presenter.onShowTransactionsByCurrencyAbbr(abbr);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigatorHolder.setNavigator(navigator);
    }

    @Override
    protected void onPause() {
        super.onPause();
        navigatorHolder.removeNavigator();
    }

    private Navigator navigator = new DefaultNavigator(this) {
        @Override
        protected void forward(Forward forwardCommand) {

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
        currencyTransfersAdapter.refreshItems(transfers);
        endlessRecyclerViewScrollListener.resetState();
    }

    @Override
    public void newTransferWasLoaded(CurrencyTransferEntity transfer) {
        currencyTransfersAdapter.addItemToBegin(transfer);
    }

    @Override
    public void setLoading(boolean loading) {

    }
}
