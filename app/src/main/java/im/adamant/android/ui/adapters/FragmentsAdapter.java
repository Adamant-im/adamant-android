package im.adamant.android.ui.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import im.adamant.android.ui.fragments.BaseFragment;

public class FragmentsAdapter extends FragmentStatePagerAdapter {
    private List<Class> classes = new ArrayList<>();
    private List<BaseFragment> fragments = new ArrayList<>();
    private Context context;

    public FragmentsAdapter(FragmentManager fm) {
        super(fm);
    }

    public FragmentsAdapter(FragmentManager fm, Context context, List<Class> classes) {
        super(fm);

        this.context = context;

        if (classes != null){
            this.classes = classes;
            for (Class clazz : classes){
                BaseFragment fragment = null;
                try {
                    fragment = (BaseFragment) clazz.newInstance();
                    fragments.add(fragment);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return classes.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        try {
            BaseFragment fragment = fragments.get(position);
            int resourceId = fragment.getActivityTitleId();

            title = context.getString(resourceId);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return title;
    }

}
