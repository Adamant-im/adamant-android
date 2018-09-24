package im.adamant.android.currencies;

import dagger.MapKey;

@MapKey
public @interface SupportedCurrencyTypeKey {
    SupportedCurrencyType value();
}
