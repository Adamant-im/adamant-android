package im.adamant.android.base;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public abstract class BaseTest {
    private Set<IdlingResource> registeredResources = new HashSet<>();

    protected void idlingBlock(IdlingResource idlingResource, Runnable runnable) {
        IdlingRegistry.getInstance().register(idlingResource);
        registeredResources.add(idlingResource);
        runnable.run();
        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    public void teardown() throws IOException {
        //if test fail, unregister all resources
        for (IdlingResource resource : registeredResources) {
            IdlingRegistry.getInstance().unregister(resource);
        }
    }
}
