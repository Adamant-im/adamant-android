package im.adamant.android.ui.holders;

import android.support.annotation.NonNull;

import im.adamant.android.ui.fragments.BaseFragment;

public class FragmentClassHolder {
    private String title;
    private Class<? extends BaseFragment> fragmentClass;

    public FragmentClassHolder(@NonNull String title, @NonNull Class<? extends BaseFragment> fragmentClass) {
        this.title = title;
        this.fragmentClass = fragmentClass;
    }

    public String getTitle() {
        return title;
    }

    public Class<? extends BaseFragment> getFragmentClass() {
        return fragmentClass;
    }
}
