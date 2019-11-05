package com.android.notice;


import android.app.Activity;
import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class App extends Application {

    private RefWatcher watcher;

    @Override
    public void onCreate() {
        super.onCreate();

        watcher = LeakCanary.install(this);
    }

    public static void watch(Activity activity) {
        App app = (App) activity.getApplication();
        app.watcher.watch(activity);
    }
}
