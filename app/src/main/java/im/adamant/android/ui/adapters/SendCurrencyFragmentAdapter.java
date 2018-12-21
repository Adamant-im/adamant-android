package im.adamant.android.ui.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import im.adamant.android.interactors.wallets.SupportedWalletFacadeType;
import im.adamant.android.interactors.wallets.WalletFacade;
import im.adamant.android.ui.entities.SendCurrencyEntity;
import im.adamant.android.ui.fragments.SendCurrencyFragment;


public class SendCurrencyFragmentAdapter extends FragmentStatePagerAdapter {
    private Map<SupportedWalletFacadeType, WalletFacade> map = new HashMap<>();
    private List<WalletFacade> items = new ArrayList<>();
    private String companionId;

    public SendCurrencyFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setItems(Map<SupportedWalletFacadeType, WalletFacade> items) {
        if (items != null) {
            this.items = new ArrayList<>(items.values());
            this.map = items;
            notifyDataSetChanged();
        }
    }

    public void setCompanionId(String companionId) {
        this.companionId = companionId;
    }

    @Override
    public Fragment getItem(int position) {
        WalletFacade walletFacade = items.get(position);

        if (walletFacade == null) {return null;}
        return SendCurrencyFragment.newInstance(walletFacade.getCurrencyType(), companionId);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        WalletFacade walletFacade = items.get(position);
        if (walletFacade != null){
            return walletFacade.getCurrencyType().name();
        } else {
            return "";
        }
    }
}
