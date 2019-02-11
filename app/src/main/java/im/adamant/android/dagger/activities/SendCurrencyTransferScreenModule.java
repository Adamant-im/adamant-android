package im.adamant.android.dagger.activities;

import java.util.Map;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import im.adamant.android.dagger.fragments.FragmentScope;
import im.adamant.android.dagger.fragments.SendCurrencyTransferFragmentModule;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
import im.adamant.android.interactors.wallets.WalletFacade;
import im.adamant.android.ui.SendFundsScreen;
import im.adamant.android.ui.adapters.SendCurrencyFragmentAdapter;
import im.adamant.android.ui.fragments.SendFundsFragment;

@Module
public abstract class SendCurrencyTransferScreenModule {
    @ActivityScope
    @Provides
    public static SendCurrencyFragmentAdapter provideFragmentAdapter(
            SendFundsScreen activity,
            Map<SupportedWalletFacadeType, WalletFacade> wallets
    ) {
        SendCurrencyFragmentAdapter sendCurrencyFragmentAdapter = new SendCurrencyFragmentAdapter(activity.getSupportFragmentManager());
        sendCurrencyFragmentAdapter.setItems(wallets);

        return sendCurrencyFragmentAdapter;
    }

    @FragmentScope
    @ContributesAndroidInjector(modules = {SendCurrencyTransferFragmentModule.class})
    public abstract SendFundsFragment createSendCurrencyFragmentInjector();
}
