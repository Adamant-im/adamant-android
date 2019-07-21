package im.adamant.android.idling_resources;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.test.espresso.IdlingResource;

public class DialogFragmentIdlingResource implements IdlingResource {

    private ResourceCallback resourceCallback;
    private FragmentManager fragmentManager;

    private String fragmentTag;

    public DialogFragmentIdlingResource(String fragmentTag, FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        this.fragmentTag = fragmentTag;
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public boolean isIdleNow() {
        boolean fragmentWasShow = isFragmentWasShow();
        if (!fragmentWasShow) {
            resourceCallback.onTransitionToIdle();
        }

        return fragmentWasShow;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;
    }

    private boolean isFragmentWasShow() {
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(fragmentTag);

        if (fragmentByTag != null) {
            return fragmentByTag.isVisible();
        } else {
            return false;
        }
    }
}
