package com.android.lib;

import android.app.Activity;
import android.view.View;

public interface INoticeView {
    View getView(Activity activity, INotice notice);
}
