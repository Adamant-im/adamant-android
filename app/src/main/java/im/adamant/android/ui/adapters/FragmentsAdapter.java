package im.adamant.android.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentsAdapter extends FragmentStatePagerAdapter {
    private List<Class> classes = new ArrayList<>();

    public FragmentsAdapter(FragmentManager fm) {
        super(fm);
    }

    public FragmentsAdapter(FragmentManager fm, List<Class> classes) {
        super(fm);

        if (classes != null){
            this.classes = classes;
        }
    }

    @Override
    public Fragment getItem(int position) {

        if((position >= 0) && (position < classes.size())){
            try {
               return (Fragment) classes.get(position).newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public int getCount() {
        return classes.size();
    }
}
