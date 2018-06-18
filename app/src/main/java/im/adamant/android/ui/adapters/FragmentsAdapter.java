package im.adamant.android.ui.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import im.adamant.android.ui.fragments.BaseFragment;
import im.adamant.android.ui.holders.FragmentClassHolder;

public class FragmentsAdapter extends FragmentStatePagerAdapter {
    private List<FragmentClassHolder> classHolders = new ArrayList<>();
    private List<BaseFragment> fragments = new ArrayList<>();

    public FragmentsAdapter(FragmentManager fm) {
        super(fm);
    }

    public FragmentsAdapter(FragmentManager fm, List<FragmentClassHolder> classHolders) {
        super(fm);

        if (classHolders != null){
            this.classHolders = classHolders;
        }
    }

    @Override
    public Fragment getItem(int position) {

        if((position >= 0) && (position < classHolders.size())){
            try {
                FragmentClassHolder holder = classHolders.get(position);
                Fragment fragment = holder.getFragmentClass().newInstance();
                fragments.add((BaseFragment) fragment);
                return fragment;
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public int getCount() {
        return classHolders.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        try {
            title = classHolders.get(position).getTitle();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return title;
    }
}
