package com.android.lib;

import android.app.Activity;

interface INoticeRegistry {

    void register(Activity activity);

    void unregister(Activity activity);

    void resume(Activity activity);

    void pause(Activity activity);
}
