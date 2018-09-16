package im.adamant.android.ui.holders;

import androidx.annotation.NonNull;

import im.adamant.android.ui.fragments.BaseFragment;

public class FragmentClassHolder {
    private int titleResource;
    private Class<? extends BaseFragment> fragmentClass;

    public FragmentClassHolder(int title, @NonNull Class<? extends BaseFragment> fragmentClass) {
        this.titleResource = title;
        this.fragmentClass = fragmentClass;
    }

    public int getTitle() {
        return titleResource;
    }

    public Class<? extends BaseFragment> getFragmentClass() {
        return fragmentClass;
    }
}
