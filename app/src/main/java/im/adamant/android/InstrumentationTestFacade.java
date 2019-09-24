package im.adamant.android;

import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.interactors.LogoutInteractor;
import im.adamant.android.interactors.SecurityInteractor;
import ru.terrakok.cicerone.Router;

public class InstrumentationTestFacade {
    private LogoutInteractor logoutInteractor;
    private SecurityInteractor securityInteractor;
    private AdamantApiWrapper adamantApiWrapper;

    public InstrumentationTestFacade(
            LogoutInteractor logoutInteractor,
            SecurityInteractor securityInteractor,
            AdamantApiWrapper adamantApiWrapper
    ) {
        this.logoutInteractor = logoutInteractor;
        this.securityInteractor = securityInteractor;
        this.adamantApiWrapper = adamantApiWrapper;
    }

    public void logout() {
        logoutInteractor.logout();
    }

    public AdamantApiWrapper getApiWrapper() {
        return adamantApiWrapper;
    }

    public SecurityInteractor getSecurityInteractor() {
        return this.securityInteractor;
    }
}
