package im.adamant.android.ui.adapters;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import im.adamant.android.ui.holders.FragmentClassHolder;

public class FragmentsAdapter extends FragmentStatePagerAdapter {
    private List<FragmentClassHolder> holders = new ArrayList<>();
    private Context context;


    public FragmentsAdapter(AppCompatActivity context, List<FragmentClassHolder> holders) {
        super(context.getSupportFragmentManager());

        this.holders = holders;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        try {
            Class clazz = holders.get(position).getFragmentClass();
            fragment = (Fragment) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return fragment;
    }


    @Override
    public int getCount() {
        return holders.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        try {
            int resourceId = holders.get(position).getTitle();
            title = context.getString(resourceId);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return title;
    }

}
