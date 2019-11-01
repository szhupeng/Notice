package com.android.lib;

import android.app.Activity;

public interface INoticeReceiver {
    void accept(Activity activity, INotice notice);

    void refuse(Activity activity);
}
