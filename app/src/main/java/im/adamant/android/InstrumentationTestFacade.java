package im.adamant.android;

import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.helpers.Settings;
import im.adamant.android.interactors.LogoutInteractor;
import im.adamant.android.interactors.SecurityInteractor;
import ru.terrakok.cicerone.Router;

public class InstrumentationTestFacade {
    private LogoutInteractor logoutInteractor;
    private SecurityInteractor securityInteractor;
    private AdamantApiWrapper adamantApiWrapper;
    private Settings settings;

    public InstrumentationTestFacade(
            LogoutInteractor logoutInteractor,
            SecurityInteractor securityInteractor,
            AdamantApiWrapper adamantApiWrapper,
            Settings settings
    ) {
        this.logoutInteractor = logoutInteractor;
        this.securityInteractor = securityInteractor;
        this.adamantApiWrapper = adamantApiWrapper;
        this.settings = settings;
    }

    public void logout() {
        logoutInteractor.logout();
    }

    public AdamantApiWrapper getApiWrapper() {
        return adamantApiWrapper;
    }

    public SecurityInteractor getSecurityInteractor() {
        return securityInteractor;
    }

    public Settings getSettings() {
        return settings;
    }
}
