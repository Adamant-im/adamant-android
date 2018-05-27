package com.dremanovich.adamant_android.dagger;

import android.content.Context;

import com.dremanovich.adamant_android.AdamantApplication;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder context(Context context);
        AppComponent build();
    }

    void inject(AdamantApplication app);
}
