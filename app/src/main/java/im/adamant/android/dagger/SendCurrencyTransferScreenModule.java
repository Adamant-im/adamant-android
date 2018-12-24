package im.adamant.android.dagger;

import android.content.Context;

import java.util.ArrayList;
import java.util.Map;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
import im.adamant.android.interactors.wallets.WalletFacade;
import im.adamant.android.ui.SendCurrencyTransferScreen;
import im.adamant.android.ui.adapters.SendCurrencyFragmentAdapter;
import im.adamant.android.ui.fragments.BottomCreateChatFragment;
import im.adamant.android.ui.fragments.SendCurrencyFragment;
import im.adamant.android.ui.presenters.SendCurrencyPresenter;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

@Module
public abstract class SendCurrencyTransferScreenModule {
    @ActivityScope
    @Provides
    public static SendCurrencyFragmentAdapter provideFragmentAdapter(
            SendCurrencyTransferScreen activity,
            Map<SupportedWalletFacadeType, WalletFacade> wallets
    ) {
        SendCurrencyFragmentAdapter sendCurrencyFragmentAdapter = new SendCurrencyFragmentAdapter(activity.getSupportFragmentManager());
        sendCurrencyFragmentAdapter.setItems(wallets);

        return sendCurrencyFragmentAdapter;
    }

    @FragmentScope
    @ContributesAndroidInjector(modules = {SendCurrencyTransferFragmentModule.class})
    public abstract SendCurrencyFragment createSendCurrencyFragmentInjector();
}
