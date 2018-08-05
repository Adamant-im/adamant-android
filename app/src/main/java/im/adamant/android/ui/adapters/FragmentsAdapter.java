package im.adamant.android.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.goterl.lazycode.lazysodium.interfaces.Base;

import java.util.ArrayList;
import java.util.List;

import im.adamant.android.ui.fragments.BaseFragment;
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
