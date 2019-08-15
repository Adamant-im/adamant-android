package im.adamant.android;

import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.interactors.LogoutInteractor;

public class InstrumentationTestFacade {
    private LogoutInteractor logoutInteractor;
    private AdamantApiWrapper adamantApiWrapper;

    public InstrumentationTestFacade(LogoutInteractor logoutInteractor, AdamantApiWrapper adamantApiWrapper) {
        this.logoutInteractor = logoutInteractor;
        this.adamantApiWrapper = adamantApiWrapper;
    }

    public void logout() {
        logoutInteractor.logout();
    }

    public AdamantApiWrapper getApiWrapper() {
        return adamantApiWrapper;
    }
}
