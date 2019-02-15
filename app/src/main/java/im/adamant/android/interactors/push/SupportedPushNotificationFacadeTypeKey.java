package im.adamant.android.interactors.push;

import dagger.MapKey;

@MapKey
public @interface SupportedPushNotificationFacadeTypeKey {
    SupportedPushNotificationFacadeType value();
}
